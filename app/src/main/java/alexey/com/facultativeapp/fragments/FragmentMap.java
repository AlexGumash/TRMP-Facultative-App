package alexey.com.facultativeapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.geometry.SubpolylineHelper;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.transport.TransportFactory;
import com.yandex.mapkit.transport.masstransit.MasstransitOptions;
import com.yandex.mapkit.transport.masstransit.MasstransitRouter;
import com.yandex.mapkit.transport.masstransit.Route;
import com.yandex.mapkit.transport.masstransit.Section;
import com.yandex.mapkit.transport.masstransit.SectionMetadata;
import com.yandex.mapkit.transport.masstransit.Session;
import com.yandex.mapkit.transport.masstransit.TimeOptions;
import com.yandex.mapkit.transport.masstransit.Transport;
import com.yandex.runtime.Error;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import alexey.com.facultativeapp.R;

public class FragmentMap extends Fragment {


    private MapView mapView;
    private MapObjectCollection mapObjects;
    private final Point ROUTE_START_LOCATION = new Point(55.435674, 37.309390); //Начальная точка маршрута
    private final Point ROUTE_END_LOCATION = new Point(55.793983, 37.701717); //Конечная точка маршрута
    private double startLat = ROUTE_START_LOCATION.getLatitude();
    private double startLong= ROUTE_START_LOCATION.getLongitude();
    private double endLat = ROUTE_END_LOCATION.getLatitude();
    private double endLong = ROUTE_END_LOCATION.getLongitude();
    private final Point TARGET_LOCATION = new Point((startLat + endLat) / 2 , (startLong +
            endLong) / 2);

    /*
    Создание View
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = view.findViewById(R.id.mapView);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*
        Перемещение карты на Москву
         */
        mapView.getMap().move(
                new CameraPosition(TARGET_LOCATION, 12.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);

        mapObjects = mapView.getMap().getMapObjects().addCollection();

        MasstransitOptions options = new MasstransitOptions(
                new ArrayList<String>(),
                new ArrayList<String>(),
                new TimeOptions());

        /*
        Создание списка точек.
        Добавляются точка начала маршрута
        и точка конца маршрута
         */
        List<RequestPoint> points = new ArrayList<>();
        points.add(new RequestPoint(ROUTE_START_LOCATION, new ArrayList<Point>(),
                RequestPointType.WAYPOINT));
        points.add(new RequestPoint(ROUTE_END_LOCATION, new ArrayList<Point>(), RequestPointType
                .WAYPOINT));

        MasstransitRouter mtRouter = TransportFactory.getInstance().createMasstransitRouter();
        mtRouter.requestRoutes(points, options, new Session.RouteListener() {
            @Override
            public void onMasstransitRoutes(@NonNull List<Route> list) {
                if (list.size() > 0) {
                    for (Section section : list.get(0).getSections()) {
                        drawSection(
                                section.getMetadata().getData(),
                                SubpolylineHelper.subpolyline(
                                        list.get(0).getGeometry(), section.getGeometry()));
                    }
                }
            }

            @Override
            public void onMasstransitRoutesError(@NonNull Error error) {
                String errorMsg = getString(R.string.unknown_error_message);

                if (error instanceof RemoteError) {
                    errorMsg = getString(R.string.remote_error_message);
                } else if (error instanceof NetworkError) {
                    errorMsg = getString(R.string.network_error_message);
                }
                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    /*
    Методы для старта и стопа activity
     */
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
    }

    /*
    Путь.
    Выбираем тип транспорта.
    Некоторые типы транспорта имеют свои цвета.
    Закрашиваем линию в соотвествующий цвет.
    Автобус - зеленый
    Метро - цвета, соответствующие цветам веток
    Остальные типы транспорта - синий
     */
    private void drawSection(SectionMetadata.SectionData data, Polyline geometry) {
        PolylineMapObject polylineMapObject = mapObjects.addPolyline(geometry);

        if (data.getTransports() != null) {
            for (Transport transport : data.getTransports()) {
                if (transport.getLine().getStyle() != null) {
                    polylineMapObject.setStrokeColor(
                            transport.getLine().getStyle().getColor() | 0xFF000000 );
                    return;
                }
            }

            HashSet<String> knownVehicleType = new HashSet<>();
            knownVehicleType.add("bus");
            knownVehicleType.add("tramway");
            for (Transport transport : data.getTransports()) {
                String sectionVehicleType = getVehicleType(transport, knownVehicleType);
                assert sectionVehicleType != null;
                if (sectionVehicleType.equals("bus")) {
                    polylineMapObject.setStrokeColor(0xFF00FF00); // зеленый
                    return;
                } else if (sectionVehicleType.equals("tramway")){
                    polylineMapObject.setStrokeColor(0xFFFF0000); // красный
                    return;
                }
            }
            polylineMapObject.setStrokeColor(0xFF0000FF); // синий
        } else { //Если участок не принадлежит общественному транспорту, то закрасить в черный
            polylineMapObject.setStrokeColor(0xFF000000);
        }
    }

    private String getVehicleType(Transport transport, HashSet<String> knownVehicleType) {
        /*
        Перебор транспорта.
        Транспорт, на котором можно преодолеть следующий участок пути,
        постоянно меняется.
        Поэтому осуществляется перебор типов транспорта, пока не найдется подходящий
         */
        for (String type : transport.getLine().getVehicleTypes()) {
            if (knownVehicleType.contains(type)) {
                return type;
            }
        }
        return null;
    }
}
