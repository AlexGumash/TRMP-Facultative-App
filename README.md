# TRMP-Facultative-App
TRMP Facultative App

# Техническое задание

## Основные требования

- Для входа в приложение использовать аутентификацю на сервере GitHub.
  Для работы с сервисом использовать GitHub API.
- Использование паттерна Navigation Drawer.
  Использовать Fragment или CustomView.
- Меню
  - Окно списка репозиториев пользователя.
  Отображение данных возможно либо с помощью WebView, либо используя RecycleView с запросами API.
  - Карты Google или Yandex.
    Отображение маршрута от дома до университета используя какой-либо вид транспорта.
  - Отображение списка контактов из телефонной книги.
  - Отображение основной информации об устройстве.
    IP, android version и т.д..
  - Отображение изменяемых значений работы одного из датчиков и получения фотографии с последующим сохранением в память.
  - Выход (logout).
  
## Задачи приложения и их описание

- Аутентификация на серевере GitHub.
  Для входа в приложение необходимо, чтобы пользователь зашел в свою учетную запись на GitHub.
- Отображение списка репозиториев пользователя.
  Реализована функция загрузки списка репозиториев пользователя и функция отображения данного списка в соответствующем окне приложения.
- Отображение маршрута от дома до университета.
  Весь маршрут разбивается на участки, соответствующие различным типам транспорта. 
  - Черная часть - пешком.
  - Зеленая часть - автобус (наземный общественный транспорт).
  - Цвета, соответствующие цветам веток метро - метро.
- Отображение списка контактов.
  Приложение запрашивает разрешение на получение подобной информации.
  Если разрешение получено, то осуществляется вывод списка контактов.
  Каждый контакт выводится парой *Имя контакта - телефон контакта*
- Информация об устройстве
  Выводимые поля:
  - IPv4
  - IPv6
  - Модель устройства
  - Версия ОС
- Работа с датчиками и камерой
  - Акселерометр
  - Взятие скриншота и сохранение его в галерею
- Выход из приложения.
  Преподполагается сброс аутентификации на GitHub
  
## Графический макет с логическими переходами

![Layout](Layout.png)

## Описание используемых API

[GitHub API](https://developer.github.com/v3/).
Было использовано 3 метода:
- Получение access token
- Получение списка репозиториев пользователя
- Получение авторизованного пользователя

[Yandex Maps](https://github.com/yandex/mapkit-android-demo/tree/master/src/main/java/com/yandex/mapkitdemo)
Благодаря данному API была реализована функция отображения маршрута от дома до университета.

[Retrofit](https://square.github.io/retrofit/)
Данная библиотека позволяет легко создавать HTTP-клиент для работы с сетью (осуществлять GET, POST запросы и т.д.)
Также была применена библиотека для автоматического распарсивания ответов с сервера в GSON.

## Описание используемого шаблона

В данном приложении сложно выделить какой-либо конкретный шаблон проектирования. В большинстве классов присутствуют признаки и модели, и представления, и презентера. Например, все фрагменты можно отнести и ко представлению, и к презентеру. Однако больше всего это напоминает MVP, так как есть классы, которые можно точно отнести к какому-либо уровню.

### Описание шаблона MVP

MVP( Model - View - Presenter) - шаблон проектирования пользовательского интерфейса, который реализует отделение бизнес логики (Model) приложения от уровня отображения. Model хранит в себе всю бизнес-логику, при необходимости получает данные из хранилища. View реализует отображение данных из Model, обращается к Presenter за обновлениями. Presenter, в свою очередь, реализует взаимодействие между Model и View. 

## Взаимодействие UI со слоем бизнес-логики

При запуске приложения неавторизованный пользователь попадает на экран авторизации.
При нажатии на кнопку "Sign in" происходит перенаправление пользователя на страницу авторизации.

    public void clickSignIn (View view) {
        signIn();
    }

    private void signIn() {
        String myUrlGit = "https://github.com/login/oauth/authorize?client_id=" + App.getCliendId() +
                "&scope=repo&redirect_uri=" + App.getRedirectUri();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(myUrlGit));
        startActivity(intent);
    }
    
После авторизации пользователь попадает на главный экран со списком репозиториев.

    private void goMainActivity() {
        Intent intent = new Intent(this, ActivityMain.class);
        startActivity(intent);
        finishAffinity();
    }
    
Навигация между фрагментами (разделами приложения) осуществляется с помощью navigation drawer.
Выбор раздела осущствляется с помощью библиотеки FragNav.

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_repo: fragNavController.switchTab(FragNavController.TAB1); break;
            case R.id.nav_map: fragNavController.switchTab(FragNavController.TAB2); break;
            case R.id.nav_contacts: fragNavController.switchTab(FragNavController.TAB3); break;
            case R.id.nav_info: fragNavController.switchTab(FragNavController.TAB4); break;
            case R.id.nav_sensor: fragNavController.switchTab(FragNavController.TAB5); break;
            case R.id.nav_logout: logOut(); break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    
При переходе на раздел со списком контактов или раздел с сенсорами приложение проверяет наличие разрешений.

Разрешение на список контактов:

    private void permissionsRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED ) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, 1);
        } else {
            showContacts();
        }
    }
    
Разрешение на использование сенсоров:

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

Изменение показаний сенсора

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
        
    void showInfo() {
        sb.setLength(0);
        sb.append("Accelerometer: ").append(format(valueAccelerometer));
        tvAccelerometer.setText(sb);
    }

При нажатии на кнопку "Take Photo" делается скриншот экрана

    btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b = Instruments.takeScreenshotOfRootView(imgViewScreen);
                imgViewScreen.setImageBitmap(b);
                Toast.makeText(getActivity(), "Screenshot is made", Toast.LENGTH_SHORT).show();
            }
        });
        
Сохранение полученного изображения в галерею

    private void saveBitmapToGallery(Bitmap bitmap, String fName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission
                (getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return;
        }

        String root = Environment.getExternalStoragePublicDirectory(Environment
                .DIRECTORY_PICTURES).toString();

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

В разделе об информации об устройстве происходит определение IP, версии ОС устройства и модели устройства.

    public static String getMobileIPAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        return  addr.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            Log.d("Log", "Ошибка при получении адреса мобильной сети");
        }
        return "";
    }

    public static String getWifiIPAddress(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        return  Formatter.formatIpAddress(ip);
    }
    
    String modelValue = Build.MODEL;
    
    String version = Build.VERSION.RELEASE;
    
При переходе в раздел со списком репозиториев просиходит загрузка списка репозиториев.

    public void loadList() {
        App.getNetClient().getRepos(App.getUsername(), new Callback<List<GitHubRepo>>() {
            @Override
            public void onResponse(Call<List<GitHubRepo>> call, Response<List<GitHubRepo>> response) {
                if (response.isSuccessful()) {
                    Log.d(LOG, "То что пришло = " + new Gson().toJson(response.body()));
                    gitHubRepoList.clear();
                    gitHubRepoList.addAll(response.body());
                    gitReposAdapter.notifyDataSetChanged();
                } else {
                    Log.d(LOG, "Код ошибки = " + response.code());
                    try {
                        Log.d(LOG, "Сообщение ошибки = " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<GitHubRepo>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

Когда пользователь переходит в раздел карты, то карты запускаются

    public void onStart() {
        super.onStart();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
    }
    
