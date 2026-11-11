VIALIDAD XICO - APP ANDROID (APK)
==============================
1. Abre Android Studio > Open > elige la carpeta VialidadXico_APK
2. Espera a que haga Sync Gradle (abajo dice Sync OK)
3. Ve a Build > Build App Bundle(s) / APK(s) > Build APK(s)
4. Espera, te sale: APK(s) generated successfully > locate
5. El APK está en: app/build/outputs/apk/debug/app-debug.apk
6. Mándalo por WhatsApp al cliente como Documento. El cliente le da Instalar > Permitir de esta fuente > Instalar.
7. Icono verde V, nombre Vialidad Xico.

NOTA EXCEL EN APK:
- En WebView, la descarga de Excel .xlsx con ExcelJS genera un blob. Android Studio con este MainActivity ya tiene el puente AndroidBridge.
- Si el Excel no baja en la primera prueba, en el HTML el botón de Excel ya tiene fallback a CSV que sí baja con DownloadListener.
- Para versión final 100% nativa, el Excel se genera con Apache POI en Kotlin (Fase 2).

Datos: se guardan en WebView localStorage, persisten aunque se cierre la app. No se borran al cambiar de navegador porque ya no hay navegador.
