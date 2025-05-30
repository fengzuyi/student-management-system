/**
 * 格式化日期时间
 * @param date 日期时间字符串或Date对象
 * @returns 格式化后的日期时间字符串，格式：YYYY-MM-DD HH:mm:ss
 */
export function formatDateTime(date: string | Date | null | undefined): string {
  if (!date) return ''
  
  const d = new Date(date)
  if (isNaN(d.getTime())) return ''

  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')

  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
} 