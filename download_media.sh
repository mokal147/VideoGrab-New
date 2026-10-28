#!/data/data/com.termux/files/usr/bin/bash
set -e
URL="$1"
MODE="$2"
START="$3"
END="$4"
OUT="$HOME/storage/downloads"
mkdir -p "$OUT"
ARGS=()
if [ "$MODE" = "audio" ]; then
  ARGS+=( -x --audio-format mp3 )
fi
if [ -n "$START" ] && [ -n "$END" ]; then
  ARGS+=( --download-sections "*${START}-${END}" --force-keyframes-at-cuts )
fi
yt-dlp "${ARGS[@]}" -o "$OUT/%(title)s.%(ext)s" "$URL"
