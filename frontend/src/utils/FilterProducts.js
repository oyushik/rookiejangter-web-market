export function FilterProducts(products, { keyword, area, category, minPrice, maxPrice }) {
  return products.filter(product => {
    // 키워드: title, content 필드에서 검색 (대소문자 무시)
    if (
      keyword &&
      !(
        (product.title && product.title.toLowerCase().includes(keyword.toLowerCase())) ||
        (product.content && product.content.toLowerCase().includes(keyword.toLowerCase()))
      )
    ) return false;

    // area: product.seller.area?.fullName 또는 product.area 등 실제 데이터 구조에 맞게 수정
    if (area) {
      const productAreaName = product.seller?.area?.areaName || '';
      if (productAreaName !== area) return false;
    }

    // category: categoryId 또는 categoryName 등 실제 데이터 구조에 맞게 비교
    if (category && category !== '전체') {
      const cat = String(category);
      if (
        String(product.categoryName) !== cat
      ) return false;
    }

    // 가격 필터
    const min = minPrice ? Number(minPrice.replace(/,/g, '')) : null;
    const max = maxPrice ? Number(maxPrice.replace(/,/g, '')) : null;
    const price = Number(product.price);

    if (min !== null && price < min) return false;
    if (max !== null && price > max) return false;

    return true;
  });
}