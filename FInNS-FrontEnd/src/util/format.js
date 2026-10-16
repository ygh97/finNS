const pad = (n) => String(n).padStart(2, '0');

// 2026-10-16
export const formatDate = (value) => {
  if (!value) return '';
  const d = new Date(value);
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
};

// 10월 16일 14:05
export const formatShortDateTime = (value) => {
  if (!value) return '';
  const d = new Date(value);
  return `${d.getMonth() + 1}월 ${d.getDate()}일 ${d.getHours()}:${pad(d.getMinutes())}`;
};

// 12,300원
export const formatWon = (amount) => `${Number(amount ?? 0).toLocaleString()}원`;
