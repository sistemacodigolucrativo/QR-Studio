#!/usr/bin/env python3
"""Edit central app-config.properties safely without modifying source files."""
import argparse
import os
import re
import tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
CONFIG = ROOT / "app-config.properties"
ALLOWED = {
    "appName", "applicationId", "versionCode", "versionName", "baseApiUrl",
    "iconName", "brandColor", "minSdk", "targetSdk", "compileSdk",
}


def check(key, value):
    if key not in ALLOWED or not value or "\n" in value or "\r" in value:
        raise ValueError("Invalid configuration field: " + key)
    if key == "applicationId" and not re.fullmatch(r"[A-Za-z][A-Za-z0-9_]*(\.[A-Za-z][A-Za-z0-9_]*)+", value):
        raise ValueError("applicationId must be a valid Android package")
    if key == "versionName" and not re.fullmatch(r"\d+\.\d+\.\d+", value):
        raise ValueError("versionName must use SemVer")
    if key == "brandColor" and not re.fullmatch(r"#[A-Fa-f0-9]{6}", value):
        raise ValueError("brandColor must be #RRGGBB")
    if key == "baseApiUrl" and not value.startswith("https://"):
        raise ValueError("baseApiUrl must use HTTPS")
    if key == "iconName" and not re.fullmatch(r"[a-z][a-z0-9_]*", value):
        raise ValueError("iconName must be an Android resource name")
    if key in {"versionCode", "minSdk", "targetSdk", "compileSdk"} and not value.isdecimal():
        raise ValueError("Invalid numeric property " + key)


def apply_updates(content, updates):
    for key, value in updates.items():
        check(key, value)
    lines = content.splitlines(keepends=True)
    found = set()
    for index, line in enumerate(lines):
        if line.startswith("#") or "=" not in line:
            continue
        key = line.split("=", 1)[0].strip()
        if key in updates:
            lines[index] = key + "=" + updates[key] + "\n"
            found.add(key)
    for key, value in updates.items():
        if key not in found:
            lines.append(key + "=" + value + "\n")
    return "".join(lines)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--file", type=Path, default=CONFIG)
    for key in sorted(ALLOWED):
        parser.add_argument("--" + key.replace("_", "-"), dest=key)
    args = parser.parse_args()
    updates = {key: getattr(args, key) for key in ALLOWED if getattr(args, key) is not None}
    if not updates:
        parser.error("Specify at least one property to update")
    original = args.file.read_text(encoding="utf-8")
    final = apply_updates(original, updates)
    with tempfile.NamedTemporaryFile("w", encoding="utf-8", dir=args.file.parent, delete=False) as temp:
        temp.write(final)
        temporary = temp.name
    os.replace(temporary, args.file)


if __name__ == "__main__":
    main()
