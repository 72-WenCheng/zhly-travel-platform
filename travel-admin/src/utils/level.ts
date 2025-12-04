/**
 * 用户等级工具函数
 * 根据升级指南的等级体系
 */

export interface LevelInfo {
  name: string
  code: number
  points: number
  color: string
  gradient?: { start: string; end: string }
}

/**
 * 等级映射表（根据升级指南）
 */
export const LEVEL_MAP: Record<number, LevelInfo> = {
  1: {
    name: '青铜旅行者',
    code: 1,
    points: 0,
    color: '#8B7355',
    gradient: { start: '#8B7355', end: '#6B5B4F' }
  },
  2: {
    name: '白银探索者',
    code: 2,
    points: 100,
    color: '#9CA3AF',
    gradient: { start: '#9CA3AF', end: '#6B7280' }
  },
  3: {
    name: '黄金游侠',
    code: 3,
    points: 500,
    color: '#F59E0B',
    gradient: { start: '#F59E0B', end: '#D97706' }
  },
  4: {
    name: '铂金旅者',
    code: 4,
    points: 2000,
    color: '#6366F1',
    gradient: { start: '#6366F1', end: '#4F46E5' }
  },
  5: {
    name: '钻石达人',
    code: 5,
    points: 5000,
    color: '#EC4899',
    gradient: { start: '#EC4899', end: '#DB2777' }
  },
  6: {
    name: '王者导师',
    code: 6,
    points: 10000,
    color: '#F97316',
    gradient: { start: '#F97316', end: '#EA580C' }
  }
}

/**
 * 根据等级代码获取等级信息
 * @param levelCode 等级代码 (1-6)
 * @returns 等级信息
 */
export function getLevelByCode(levelCode: number | null | undefined): LevelInfo {
  if (!levelCode || levelCode < 1) {
    return LEVEL_MAP[1] // 默认青铜旅行者
  }
  if (levelCode > 6) {
    return LEVEL_MAP[6] // 最高王者导师
  }
  return LEVEL_MAP[levelCode] || LEVEL_MAP[1]
}

/**
 * 根据积分获取等级信息
 * @param points 积分
 * @returns 等级信息
 */
export function getLevelByPoints(points: number | null | undefined): LevelInfo {
  if (!points || points < 0) {
    return LEVEL_MAP[1]
  }
  
  // 从高到低查找对应的等级
  const sortedLevels = Object.values(LEVEL_MAP).sort((a, b) => b.points - a.points)
  for (const level of sortedLevels) {
    if (points >= level.points) {
      return level
    }
  }
  
  return LEVEL_MAP[1]
}

/**
 * 根据等级名称获取等级信息（兼容旧的等级名称）
 * @param levelName 等级名称
 * @returns 等级信息
 */
export function getLevelByName(levelName: string | null | undefined): LevelInfo {
  if (!levelName) {
    return LEVEL_MAP[1]
  }
  
  // 精确匹配
  for (const level of Object.values(LEVEL_MAP)) {
    if (level.name === levelName) {
      return level
    }
  }
  
  // 模糊匹配（兼容旧的等级名称）
  const nameLower = levelName.toLowerCase()
  if (nameLower.includes('青铜') || nameLower.includes('新手')) {
    return LEVEL_MAP[1]
  }
  if (nameLower.includes('白银') || nameLower.includes('探险家')) {
    return LEVEL_MAP[2]
  }
  if (nameLower.includes('黄金')) {
    return LEVEL_MAP[3]
  }
  if (nameLower.includes('铂金')) {
    return LEVEL_MAP[4]
  }
  if (nameLower.includes('钻石')) {
    return LEVEL_MAP[5]
  }
  if (nameLower.includes('王者') || nameLower.includes('导师')) {
    return LEVEL_MAP[6]
  }
  
  return LEVEL_MAP[1]
}

