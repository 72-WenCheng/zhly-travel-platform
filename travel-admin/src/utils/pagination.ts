export const resolvePaginationTotal = (data: any, fallbackList?: any[]): number => {
  const fallbackLength =
    Array.isArray(fallbackList) && fallbackList.length > 0
      ? fallbackList.length
      : Array.isArray(data?.list) && data.list.length > 0
        ? data.list.length
        : Array.isArray(data?.records) && data.records.length > 0
          ? data.records.length
          : 0

  const candidates = [
    data?.total,
    data?.totalCount,
    data?.count,
    data?.recordCount,
    data?.recordsTotal,
    data?.pageInfo?.total
  ]

  for (const item of candidates) {
    if (typeof item === 'undefined' || item === null) continue
    const parsed = Number(item)
    if (Number.isNaN(parsed)) continue

    if (parsed > 0) {
      return parsed
    }

    if (parsed === 0 && fallbackLength > 0) {
      return fallbackLength
    }

    return parsed
  }

  if (fallbackLength > 0) {
    return fallbackLength
  }

  return 0
}


