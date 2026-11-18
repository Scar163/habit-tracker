import os, re, shutil, glob

PKG = "com.gor.habittracker"
MAIN = "android/app/src/main"
JAVA = MAIN + "/java/" + PKG.replace(".", "/")
RES = MAIN + "/res"

os.makedirs(JAVA, exist_ok=True)
for f in ("MainActivity.java", "WidgetBridge.java", "HabitWidgetProvider.java"):
    shutil.copy("native/" + f, JAVA + "/" + f)

for d in ("layout", "xml", "drawable"):
    os.makedirs(RES + "/" + d, exist_ok=True)
shutil.copy("native/widget_habits.xml", RES + "/layout/widget_habits.xml")
shutil.copy("native/widget_info.xml", RES + "/xml/widget_info.xml")
shutil.copy("native/widget_bg.xml", RES + "/drawable/widget_bg.xml")
shutil.copy("native/ic_stat.xml", RES + "/drawable/ic_stat.xml")

# manifest: permissions + widget receiver
mp = MAIN + "/AndroidManifest.xml"
m = open(mp, encoding="utf-8").read()
perms = (
    '    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />\n'
    '    <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />\n'
    '    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />\n'
    '    <uses-permission android:name="android.permission.VIBRATE" />\n'
)
receiver = (
    '        <receiver android:name="' + PKG + '.HabitWidgetProvider" android:exported="true">\n'
    '            <intent-filter><action android:name="android.appwidget.action.APPWIDGET_UPDATE" /></intent-filter>\n'
    '            <meta-data android:name="android.appwidget.provider" android:resource="@xml/widget_info" />\n'
    '        </receiver>\n'
)
if "HabitWidgetProvider" not in m:
    m = m.replace("<application", perms + "    <application", 1)
    m = m.replace("</application>", receiver + "    </application>", 1)
open(mp, "w", encoding="utf-8").write(m)

# launcher icons: use our icon, drop adaptive xml so legacy png is used
icon = "icon-192.png"
shutil.rmtree(RES + "/mipmap-anydpi-v26", ignore_errors=True)
for d in glob.glob(RES + "/mipmap-*"):
    for n in ("ic_launcher", "ic_launcher_round", "ic_launcher_foreground"):
        for old in glob.glob(d + "/" + n + ".*"):
            os.remove(old)
        shutil.copy(icon, d + "/" + n + ".png")

# versionCode from run number so updates install over older builds
gp = "android/app/build.gradle"
g = open(gp, encoding="utf-8").read()
run = os.environ.get("RUN", "1")
g = re.sub(r"versionCode \d+", "versionCode " + run, g)
g = re.sub(r'versionName "[^"]*"', 'versionName "1.0.' + run + '"', g)
open(gp, "w", encoding="utf-8").write(g)
print("patched android project")
