export function PriceInput(val) {
  // 숫자만 남기기
  let num = val.replace(/[^0-9]/g, '');
  if (num === '') return '';
  // 0으로 시작하는 것 방지
  num = num.replace(/^0+/, '');
  if (num === '') return '0';
  // 세 자리마다 콤마
  return Number(num);
}
