export const planTagPalette: Record<string, {
  text: string
  bg: string
  hoverBg: string
  border: string
  shadow: string
  hoverShadow: string
}> = {
  摄影: {
    text: '#3D5AFE',
    bg: 'linear-gradient(120deg, rgba(61,90,254,0.08), rgba(61,90,254,0.18))',
    hoverBg: 'linear-gradient(120deg, rgba(61,90,254,0.18), rgba(61,90,254,0.28))',
    border: 'rgba(61,90,254,0.35)',
    shadow: '0 6px 16px rgba(61,90,254,0.12)',
    hoverShadow: '0 10px 20px rgba(61,90,254,0.2)'
  },
  美食: {
    text: '#FF7043',
    bg: 'linear-gradient(120deg, rgba(255,112,67,0.12), rgba(255,152,67,0.18))',
    hoverBg: 'linear-gradient(120deg, rgba(255,112,67,0.22), rgba(255,152,67,0.32))',
    border: 'rgba(255,140,102,0.4)',
    shadow: '0 6px 16px rgba(255,140,102,0.15)',
    hoverShadow: '0 10px 20px rgba(255,140,102,0.25)'
  },
  文化: {
    text: '#8E24AA',
    bg: 'linear-gradient(120deg, rgba(142,36,170,0.1), rgba(171,71,188,0.2))',
    hoverBg: 'linear-gradient(120deg, rgba(142,36,170,0.2), rgba(171,71,188,0.3))',
    border: 'rgba(142,36,170,0.35)',
    shadow: '0 6px 16px rgba(142,36,170,0.15)',
    hoverShadow: '0 10px 20px rgba(142,36,170,0.25)'
  },
  自然风光: {
    text: '#26A69A',
    bg: 'linear-gradient(120deg, rgba(38,166,154,0.12), rgba(77,182,172,0.2))',
    hoverBg: 'linear-gradient(120deg, rgba(38,166,154,0.22), rgba(77,182,172,0.32))',
    border: 'rgba(38,166,154,0.35)',
    shadow: '0 6px 16px rgba(38,166,154,0.15)',
    hoverShadow: '0 10px 20px rgba(38,166,154,0.25)'
  },
  历史: {
    text: '#5D4037',
    bg: 'linear-gradient(120deg, rgba(93,64,55,0.08), rgba(141,110,99,0.18))',
    hoverBg: 'linear-gradient(120deg, rgba(93,64,55,0.18), rgba(141,110,99,0.28))',
    border: 'rgba(93,64,55,0.35)',
    shadow: '0 6px 16px rgba(93,64,55,0.12)',
    hoverShadow: '0 10px 20px rgba(93,64,55,0.22)'
  },
  建筑: {
    text: '#0277BD',
    bg: 'linear-gradient(120deg, rgba(2,119,189,0.1), rgba(41,182,246,0.18))',
    hoverBg: 'linear-gradient(120deg, rgba(2,119,189,0.2), rgba(41,182,246,0.28))',
    border: 'rgba(2,119,189,0.35)',
    shadow: '0 6px 16px rgba(2,119,189,0.12)',
    hoverShadow: '0 10px 20px rgba(2,119,189,0.22)'
  },
  徒步: {
    text: '#43A047',
    bg: 'linear-gradient(120deg, rgba(67,160,71,0.12), rgba(102,187,106,0.2))',
    hoverBg: 'linear-gradient(120deg, rgba(67,160,71,0.22), rgba(102,187,106,0.32))',
    border: 'rgba(67,160,71,0.35)',
    shadow: '0 6px 16px rgba(67,160,71,0.15)',
    hoverShadow: '0 10px 20px rgba(67,160,71,0.25)'
  },
  温泉: {
    text: '#FF6F61',
    bg: 'linear-gradient(120deg, rgba(255,111,97,0.12), rgba(255,138,128,0.2))',
    hoverBg: 'linear-gradient(120deg, rgba(255,111,97,0.22), rgba(255,138,128,0.32))',
    border: 'rgba(255,111,97,0.4)',
    shadow: '0 6px 16px rgba(255,111,97,0.15)',
    hoverShadow: '0 10px 20px rgba(255,111,97,0.25)'
  },
  博物馆: {
    text: '#546E7A',
    bg: 'linear-gradient(120deg, rgba(84,110,122,0.1), rgba(120,144,156,0.2))',
    hoverBg: 'linear-gradient(120deg, rgba(84,110,122,0.2), rgba(120,144,156,0.3))',
    border: 'rgba(84,110,122,0.35)',
    shadow: '0 6px 16px rgba(84,110,122,0.12)',
    hoverShadow: '0 10px 20px rgba(84,110,122,0.22)'
  },
  夜生活: {
    text: '#9C27B0',
    bg: 'linear-gradient(120deg, rgba(156,39,176,0.12), rgba(206,147,216,0.24))',
    hoverBg: 'linear-gradient(120deg, rgba(156,39,176,0.22), rgba(206,147,216,0.34))',
    border: 'rgba(156,39,176,0.35)',
    shadow: '0 6px 16px rgba(156,39,176,0.15)',
    hoverShadow: '0 10px 20px rgba(156,39,176,0.25)'
  },
  夜景: {
    text: '#3949AB',
    bg: 'linear-gradient(120deg, rgba(57,73,171,0.12), rgba(92,107,192,0.22))',
    hoverBg: 'linear-gradient(120deg, rgba(57,73,171,0.22), rgba(92,107,192,0.32))',
    border: 'rgba(57,73,171,0.35)',
    shadow: '0 6px 16px rgba(57,73,171,0.15)',
    hoverShadow: '0 10px 20px rgba(57,73,171,0.25)'
  },
  购物: {
    text: '#FFB300',
    bg: 'linear-gradient(120deg, rgba(255,179,0,0.14), rgba(255,213,79,0.24))',
    hoverBg: 'linear-gradient(120deg, rgba(255,179,0,0.24), rgba(255,213,79,0.34))',
    border: 'rgba(255,179,0,0.4)',
    shadow: '0 6px 16px rgba(255,179,0,0.15)',
    hoverShadow: '0 10px 20px rgba(255,179,0,0.25)'
  },
  度假: {
    text: '#0097A7',
    bg: 'linear-gradient(120deg, rgba(0,151,167,0.12), rgba(0,188,212,0.22))',
    hoverBg: 'linear-gradient(120deg, rgba(0,151,167,0.22), rgba(0,188,212,0.32))',
    border: 'rgba(0,151,167,0.35)',
    shadow: '0 6px 16px rgba(0,151,167,0.15)',
    hoverShadow: '0 10px 20px rgba(0,151,167,0.25)'
  },
  情侣: {
    text: '#EC407A',
    bg: 'linear-gradient(120deg, rgba(236,64,122,0.12), rgba(240,98,146,0.24))',
    hoverBg: 'linear-gradient(120deg, rgba(236,64,122,0.22), rgba(240,98,146,0.34))',
    border: 'rgba(236,64,122,0.35)',
    shadow: '0 6px 16px rgba(236,64,122,0.15)',
    hoverShadow: '0 10px 20px rgba(236,64,122,0.25)'
  },
  亲子: {
    text: '#26C6DA',
    bg: 'linear-gradient(120deg, rgba(38,198,218,0.12), rgba(77,208,225,0.24))',
    hoverBg: 'linear-gradient(120deg, rgba(38,198,218,0.22), rgba(77,208,225,0.34))',
    border: 'rgba(38,198,218,0.35)',
    shadow: '0 6px 16px rgba(38,198,218,0.15)',
    hoverShadow: '0 10px 20px rgba(38,198,218,0.25)'
  },
  休闲: {
    text: '#7E57C2',
    bg: 'linear-gradient(120deg, rgba(126,87,194,0.12), rgba(149,117,205,0.24))',
    hoverBg: 'linear-gradient(120deg, rgba(126,87,194,0.22), rgba(149,117,205,0.34))',
    border: 'rgba(126,87,194,0.35)',
    shadow: '0 6px 16px rgba(126,87,194,0.15)',
    hoverShadow: '0 10px 20px rgba(126,87,194,0.25)'
  }
}

export const planTagOptions = Object.keys(planTagPalette)

