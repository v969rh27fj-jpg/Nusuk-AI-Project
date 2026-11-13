import re, glob, os, sys, xml.dom.minidom as md
root = sys.argv[1]
res = f"{root}/app/src/main/res"
errors = []

# ---- defined resources
defined = {k: set() for k in "string color drawable layout menu mipmap style id xml dimen".split()}
for f in glob.glob(f"{res}/values*/*.xml"):
    d = md.parse(f)
    for tag, kind in (("string","string"),("color","color"),("style","style"),("dimen","dimen")):
        for e in d.getElementsByTagName(tag):
            n = e.getAttribute("name")
            defined[kind].add(n)
            if kind == "style": defined["style"].add(n.replace(".", "_"))
for kind in ("drawable","layout","menu","mipmap","xml"):
    for f in glob.glob(f"{res}/{kind}*/*"):
        defined[kind].add(os.path.splitext(os.path.basename(f))[0])
layout_ids = {}
for f in glob.glob(f"{res}/*/*.xml"):
    s = open(f, encoding="utf-8").read()
    ids = set(re.findall(r'@\+id/(\w+)', s))
    defined["id"] |= ids
    if "/layout/" in f: layout_ids[os.path.splitext(os.path.basename(f))[0]] = ids
    md.parse(f)  # well-formed

# ---- references from XML
for f in glob.glob(f"{res}/*/*.xml") + [f"{root}/app/src/main/AndroidManifest.xml"]:
    s = open(f, encoding="utf-8").read()
    for kind, name in re.findall(r'@(string|color|drawable|layout|menu|mipmap|style|xml|dimen)/([\w.]+)', s):
        key = name.replace(".", "_") if kind == "style" else name
        if key not in defined[kind] and name not in defined[kind]:
            errors.append(f"{os.path.relpath(f, root)}: missing @{kind}/{name}")
    for name in re.findall(r'@id/(\w+)', s):
        if name not in defined["id"]: errors.append(f"{f}: missing @id/{name}")

# ---- references from Kotlin
def camel(n):
    parts = n.split("_")
    return parts[0] + "".join(p.capitalize() for p in parts[1:])
def binding_to_layout(b):
    snake = re.sub(r'(?<!^)(?=[A-Z])', '_', b[:-len("Binding")]).lower()
    return snake
kt_files = glob.glob(f"{root}/app/src/main/java/**/*.kt", recursive=True)
all_layout_binding_props = {}
for lay, ids in layout_ids.items():
    all_layout_binding_props[lay] = {camel(i) for i in ids}
for f in kt_files:
    s = open(f, encoding="utf-8").read()
    for kind, name in re.findall(r'\bR\.(string|color|drawable|layout|menu|mipmap|style|id|xml)\.(\w+)', s):
        if name not in defined[kind] and name.replace("_",".") not in defined[kind]:
            errors.append(f"{os.path.relpath(f, root)}: missing R.{kind}.{name}")
    used = set(re.findall(r'(\w+Binding)\b', s))
    used = {u for u in used if u.startswith(("Fragment","Activity","Item"))}
    props = set()
    for m in re.finditer(r'\b(?:binding|row|holder\.binding)\.(\w+)', s): props.add(m.group(1))
    for m in re.finditer(r'with\(holder\.binding\)', s): pass
    layouts = [binding_to_layout(u) for u in used]
    if layouts:
        known = set().union(*[all_layout_binding_props.get(l, set()) for l in layouts])
        for u in used:
            if binding_to_layout(u) not in layout_ids: errors.append(f"{f}: binding {u} has no layout")
        extra = {"root"}
        for p in props:
            if p not in known and p not in extra:
                errors.append(f"{os.path.relpath(f, root)}: binding property '{p}' not in layouts {layouts}")
    # package declaration vs path
    pkg = re.search(r'^package ([\w.]+)', s, re.M).group(1)
    rel = os.path.dirname(os.path.relpath(f, f"{root}/app/src/main/java")).replace("/", ".")
    if pkg != rel: errors.append(f"{f}: package {pkg} != folder {rel}")
    # brace/paren balance (rough, ignores strings)
    t = re.sub(r'"(\\.|[^"\\])*"', '""', s)
    t = re.sub(r"'(\\.|[^'\\])'", "''", t)
    t = re.sub(r'//.*', '', t); t = re.sub(r'/\*.*?\*/', '', t, flags=re.S)
    for a, b in ("{}", "()", "[]"):
        if t.count(a) != t.count(b): errors.append(f"{os.path.relpath(f, root)}: unbalanced {a}{b}")

# ---- manifest class names
man = open(f"{root}/app/src/main/AndroidManifest.xml").read()
for cls in re.findall(r'android:name="(\.[\w.]+)"', man):
    path = f"{root}/app/src/main/java/com/nusukai/app/" + cls[1:].replace(".", "/") + ".kt"
    if not os.path.exists(path): errors.append(f"manifest class {cls} not found")
# ---- string parity
sets = []
for d in ("values","values-en","values-ar"):
    doc = md.parse(f"{res}/{d}/strings.xml")
    sets.append({e.getAttribute("name") for e in doc.getElementsByTagName("string")})
if not (sets[0] == sets[1] == sets[2]): errors.append("string keys differ between languages")
# ---- hardcoded secrets
for f in glob.glob(f"{root}/**/*", recursive=True):
    if os.path.isfile(f) and f.endswith((".kt",".kts",".xml",".js",".json",".yml",".md",".properties",".example")):
        if "node_modules" in f: continue
        s = open(f, encoding="utf-8", errors="ignore").read()
        if re.search(r'sk-[A-Za-z0-9_-]{20,}|AIza[0-9A-Za-z_-]{30,}|api[_-]?key\s*=\s*"[^"]{8,}"', s):
            errors.append(f"possible secret in {f}")
print("\n".join(errors) if errors else "ALL CHECKS PASSED")
print("kotlin files:", len(kt_files), "| layouts:", len(layout_ids))
