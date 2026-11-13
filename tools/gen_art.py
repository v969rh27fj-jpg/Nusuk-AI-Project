import math, os, sys
from PIL import Image, ImageDraw
root = sys.argv[1]
res = f"{root}/app/src/main/res"

def octagram(cx, cy, r):
    """Path data for an 8-pointed star made of two overlapping squares."""
    def square(rot):
        pts = [(cx + r*math.cos(math.radians(rot + 90*i)), cy + r*math.sin(math.radians(rot + 90*i))) for i in range(4)]
        return "M" + " L".join(f"{x:.2f},{y:.2f}" for x, y in pts) + " Z"
    return square(0) + " " + square(45)

def write(p, s):
    os.makedirs(os.path.dirname(p), exist_ok=True)
    open(p, "w", encoding="utf-8").write(s)

# --- launcher / splash vectors -------------------------------------------------
fg = f'''<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp" android:height="108dp" android:viewportWidth="108" android:viewportHeight="108">
    <path android:fillColor="#C59F52" android:pathData="{octagram(54,54,27)}"/>
    <path android:fillColor="#0B4D3A" android:pathData="{octagram(54,54,17)}"/>
    <path android:fillColor="#E3C77E" android:pathData="M54,44a10,10 0 1 0 0,20a10,10 0 1 0 0,-20z"/>
    <path android:fillColor="#0B4D3A" android:pathData="M49,58l3,-9l2,0l3,9l-2,0l-0.6,-2l-3,0l-0.6,2z"/>
</vector>
'''
write(f"{res}/drawable/ic_launcher_foreground.xml", fg)
write(f"{res}/drawable/ic_splash_icon.xml", fg)
write(f"{res}/drawable/ic_launcher_background.xml", '''<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <gradient android:angle="45" android:startColor="#0D5C45" android:endColor="#073A2B"/>
</shape>
''')
adaptive = '''<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
'''
write(f"{res}/mipmap-anydpi-v26/ic_launcher.xml", adaptive)
write(f"{res}/mipmap-anydpi-v26/ic_launcher_round.xml", adaptive)

# header pattern (Islamic geometric, subtle gold stars)
stars = []
for row, y in enumerate([20, 80, 140, 200]):
    for col in range(0, 7):
        x = col*60 + (30 if row % 2 else 0)
        stars.append(octagram(x, y, 18))
write(f"{res}/drawable/ic_pattern.xml", f'''<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="360dp" android:height="220dp" android:viewportWidth="360" android:viewportHeight="220">
    <path android:fillColor="#26C59F52" android:pathData="{' '.join(stars)}"/>
</vector>
''')
write(f"{res}/drawable/bg_header.xml", '''<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <gradient android:angle="315" android:startColor="#0D5C45" android:endColor="#073A2B"/>
    <corners android:bottomLeftRadius="32dp" android:bottomRightRadius="32dp"/>
</shape>
''')
write(f"{res}/drawable/bg_gold_chip.xml", '''<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <solid android:color="#26C59F52"/>
    <corners android:radius="12dp"/>
</shape>
''')

# --- simple icons ---------------------------------------------------------------
icons = {
 "ic_nav_home": ("nonZero","M10,20v-6h4v6h5v-8h3L12,3 2,12h3v8z"),
 "ic_nav_learn": ("nonZero","M3,5h8v14H3zM13,5h8v14h-8z"),
 "ic_nav_ai": ("nonZero","M12,2l2.4,7.6L22,12l-7.6,2.4L12,22l-2.4,-7.6L2,12l7.6,-2.4z"),
 "ic_nav_quiz": ("evenOdd","M12,2a10,10 0 1 0 0,20a10,10 0 1 0 0,-20zM11,17h2v2h-2zM12,6a4,4 0 0 0 -4,4h2a2,2 0 1 1 2,2c-1,0 -1,0.8 -1,2h2c0,-0.8 0.5,-1 1,-1.4A4,4 0 0 0 12,6z"),
 "ic_nav_profile": ("nonZero","M12,12a4,4 0 1 0 0,-8a4,4 0 1 0 0,8zM4,20c0,-4 4,-6 8,-6s8,2 8,6z"),
 "ic_settings": ("evenOdd","M12,8a4,4 0 1 0 0,8a4,4 0 1 0 0,-8zM10.5,2h3l0.5,2.5 2,0.9 2.2,-1.4 2.1,2.1 -1.4,2.2 0.9,2 2.5,0.5v3l-2.5,0.5 -0.9,2 1.4,2.2 -2.1,2.1 -2.2,-1.4 -2,0.9 -0.5,2.5h-3l-0.5,-2.5 -2,-0.9 -2.2,1.4 -2.1,-2.1 1.4,-2.2 -0.9,-2L2,13.5v-3l2.5,-0.5 0.9,-2L4,5.8 6.1,3.7l2.2,1.4 2,-0.9z"),
 "ic_send": ("nonZero","M2,21l21,-9L2,3v7l15,2 -15,2z"),
 "ic_copy": ("nonZero","M16,1H4a2,2 0 0 0 -2,2v14h2V3h12zM19,5H8a2,2 0 0 0 -2,2v14a2,2 0 0 0 2,2h11a2,2 0 0 0 2,-2V7a2,2 0 0 0 -2,-2zM19,21H8V7h11z"),
 "ic_share": ("nonZero","M18,16a3,3 0 0 0 -2,0.8l-7,-4.1a3,3 0 0 0 0,-1.4l7,-4.1A3,3 0 1 0 15,5a3,3 0 0 0 0.1,0.7l-7,4.1a3,3 0 1 0 0,4.4l7.1,4.1a3,3 0 1 0 2.8,-2.3z"),
 "ic_delete": ("nonZero","M6,19a2,2 0 0 0 2,2h8a2,2 0 0 0 2,-2V7H6zM19,4h-3.5l-1,-1h-5l-1,1H5v2h14z"),
 "ic_person_large": ("nonZero","M12,12a4,4 0 1 0 0,-8a4,4 0 1 0 0,8zM4,20c0,-4 4,-6 8,-6s8,2 8,6z"),
}
for name, (fill, path) in icons.items():
    write(f"{res}/drawable/{name}.xml", f'''<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">
    <path android:fillColor="#FF000000" android:fillType="{fill}" android:pathData="{path}"/>
</vector>
''')

# --- legacy PNG launcher icons (Android 6-7) ------------------------------------
def star_pts(cx, cy, r, rot):
    return [(cx + r*math.cos(math.radians(rot + 90*i)), cy + r*math.sin(math.radians(rot + 90*i))) for i in range(4)]
def make(size, round_):
    S = size*4
    im = Image.new("RGBA", (S, S), (0,0,0,0))
    d = ImageDraw.Draw(im)
    bg = (11, 77, 58, 255)
    if round_: d.ellipse([0,0,S-1,S-1], fill=bg)
    else: d.rounded_rectangle([0,0,S-1,S-1], radius=int(S*0.22), fill=bg)
    c = S/2
    d.polygon(star_pts(c,c,S*0.34,0), fill=(197,159,82,255))
    d.polygon(star_pts(c,c,S*0.34,45), fill=(197,159,82,255))
    d.ellipse([c-S*0.2, c-S*0.2, c+S*0.2, c+S*0.2], fill=(11,77,58,255))
    d.ellipse([c-S*0.12, c-S*0.12, c+S*0.12, c+S*0.12], fill=(227,199,126,255))
    return im.resize((size, size), Image.LANCZOS)
for dpi, px in {"mdpi":48,"hdpi":72,"xhdpi":96,"xxhdpi":144,"xxxhdpi":192}.items():
    make(px, False).save(f"{res}/mipmap-{dpi}/ic_launcher.png")
    make(px, True).save(f"{res}/mipmap-{dpi}/ic_launcher_round.png")
print("art generated")
