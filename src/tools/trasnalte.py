import re, os

orig_dir = "game/"          # original .rpy files (Russian)
tl_dir   = "game/tl/english"       # translation files (same filenames)
out_dir  = "game/scripts_english"  # output directory
os.makedirs(out_dir, exist_ok=True)

def load_dialogue_from_tl(path):
    dialogue_lines = []
    with open(path, encoding="utf-8") as f:
        data = f.read()

    # Extract translated dialogue (skip `strings:` blocks)
    for block in re.finditer(r'translate english [\w\d_]+:\s+([\s\S]*?)(?=\ntranslate|\Z)', data):
        for l in block.group(1).splitlines():
            l = l.strip()
            if not l or l.startswith("#"):
                continue
            if '"' in l:  # keep only lines with dialogue
                dialogue_lines.append(l)
    return dialogue_lines

# Regex for original dialogue lines: character "..."
dialogue_re = re.compile(r'^(\s*)(\w+)\s+"(.+)"')

for fname in os.listdir(orig_dir):
    if not fname.endswith(".rpy"):
        continue

    orig_path = os.path.join(orig_dir, fname)
    tl_path   = os.path.join(tl_dir, fname)
    out_path  = os.path.join(out_dir, fname)

    if not os.path.exists(tl_path):
        print(f"⚠️ No TL for {fname}, skipping")
        continue

    tl_dialogues = load_dialogue_from_tl(tl_path)
    dialogue_index = 0
    out_lines = []

    with open(orig_path, encoding="utf-8") as f:
        for line in f:
            m = dialogue_re.match(line)
            if m and dialogue_index < len(tl_dialogues):
                indent, speaker, _ = m.groups()

                # get next English line from TL
                eng_line = tl_dialogues[dialogue_index]
                eng_text_match = re.search(r'"(.*)"', eng_line)
                eng_text = eng_text_match.group(1) if eng_text_match else _

                out_lines.append(f'{indent}{speaker} "{eng_text}"\n')
                dialogue_index += 1
            else:
                out_lines.append(line)

    with open(out_path, "w", encoding="utf-8") as f:
        f.writelines(out_lines)

    print(f"✅ Processed {fname} — replaced {dialogue_index} lines")
