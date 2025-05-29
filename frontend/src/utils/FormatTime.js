export function FormatTime(createdAt) {
  const now = new Date();
  const created = new Date(createdAt);

  const isSameDay =
    now.getFullYear() === created.getFullYear() &&
    now.getMonth() === created.getMonth() &&
    now.getDate() === created.getDate();

  if (isSameDay) {
    const diffMs = now - created;
    const diffSec = Math.floor(diffMs / 1000);
    if (diffSec < 60) return `${diffSec}초 전`;
    const diffMin = Math.floor(diffSec / 60);
    if (diffMin < 60) return `${diffMin}분 전`;
    const diffHour = Math.floor(diffMin / 60);
    return `${diffHour}시간 전`;
  } else {
    const y = String(created.getFullYear()).slice(2);
    const m = String(created.getMonth() + 1).padStart(2, '0');
    const d = String(created.getDate()).padStart(2, '0');
    return `${y}-${m}-${d}`;
  }
}