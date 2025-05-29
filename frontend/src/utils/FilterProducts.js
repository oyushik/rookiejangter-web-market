export function FilterProducts(products, { keyword, area, category, minPrice, maxPrice }) {
  return products.filter(product => {
    if (product.status !== "SALE") return false;
    if (keyword && !product.title.includes(keyword) && !product.description.includes(keyword)) return false;
    if (area && product.area !== area) return false;
    if (category && product.category !== category) return false;
    if (minPrice && product.price < Number(minPrice)) return false;
    if (maxPrice && product.price > Number(maxPrice)) return false;
    return true;
  });
}