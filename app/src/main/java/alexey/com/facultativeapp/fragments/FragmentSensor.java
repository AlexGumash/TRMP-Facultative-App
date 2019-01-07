package alexey.com.facultativeapp.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

import alexey.com.facultativeapp.R;
import alexey.com.facultativeapp.helpers.Instruments;

public class FragmentSensor extends Fragment {

    private TextView tvAccelerometer;
    private SensorManager sensorManager;
    private Sensor sensor;
    private StringBuilder sb = new StringBuilder();
    private Bitmap b;
    private SensorEventListener listener;
    private float[] valueAccelerometer = new float[3];

    private Button btnTakePhoto;
    private Button btnSavePhoto;
    private ImageView imgViewScreen;


    /*
    Создание View по файлу fragment_sensor
    Настройка сенсора - аксилерометра
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_sensor, container, false);

        tvAccelerometer = v.findViewById(R.id.tvAccelerometer);
        btnSavePhoto = v.findViewById(R.id.btnSavePhoto);
        btnTakePhoto = v.findViewById(R.id.btnTakePhoto);
        imgViewScreen = v.findViewById(R.id.imgViewScreen);

        sensorManager = (SensorManager) (Objects.requireNonNull(getActivity())).getSystemService
                (Context.SENSOR_SERVICE);
        assert sensorManager != null;
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        /*
        Проверка наличия необходимых разрешений
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        /*
        Взятие скриншота
         */
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b = Instruments.takeScreenshotOfRootView(imgViewScreen);
                imgViewScreen.setImageBitmap(b);
                Toast.makeText(getActivity(), "Screenshot is made", Toast.LENGTH_SHORT).show();
            }
        });

        /*
        Сохранение скриншота
         */
        btnSavePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBitmapToGallery(b, String.valueOf(System.currentTimeMillis()));
                Toast.makeText(getActivity(), "Screenshot is saved", Toast.LENGTH_SHORT).show();

            }
        });

        /*
        Аксилерометр
         */
        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                System.arraycopy(event.values, 0, valueAccelerometer, 0, 3);
                showInfo();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    /*
    Сохранение изображения в галерею
     */
    private void saveBitmapToGallery(Bitmap bitmap, String fName) {
        /*
        Если имеются разрешения
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission
                (getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return;
        }

        /*
        Адрес корневого каталога
         */
        String root = Environment.getExternalStoragePublicDirectory(Environment
                .DIRECTORY_PICTURES).toString();

        /*
        Создаем новую папку,
        создаем новый файл,
        записываем в него фотографию,
        сканируем систему на предмет новых файлов
         */
        File myDir = new File(root + "/Screen");
        myDir.mkdirs();
        fName += ".jpg";

        File file = new File(myDir, fName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        MediaScannerConnection.scanFile(getActivity(), new String[]{file.toString()}, null, new
                MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listener);
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    void showInfo() {
        sb.setLength(0);
        sb.append("Accelerometer: ").append(format(valueAccelerometer));
        tvAccelerometer.setText(sb);
    }

    //В инете нашел )))
    @SuppressLint("DefaultLocale")
    String format(float values[]) {
        return String.format("%1$.1f\t\t%2$.1f\t\t%3$.1f", values[0], values[1],
                values[2]);
    }
}
