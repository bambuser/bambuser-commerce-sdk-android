package com.bambuser.commerce_sdk_demo_app.data

object ProductHydrationDataSource {

    val sampleProducts: Map<String, HydratedProduct> = mapOf(
        "1222" to HydratedProduct(
            sku = "1222",
            name = "Sunlit Glow Bronzer",
            brandName = "Sunlit",
            introduction = "Get that sun-kissed glow with our Sunlit Glow Bronzer.",
            description = "Get that sun-kissed glow with our Sunlit Glow Bronzer. Effortlessly blendable and available in versatile shades.",
            variations = listOf(
                Variation(
                    sku = "1222-bronzer",
                    name = "Sunlit Glow Bronzer",
                    colorName = "bronzer",
                    imageUrls = listOf("https://cdn.prod.website-files.com/66c31044a23e58e719bc6ffb/66c33d86064576f49e7b792c_Makeup_2.webp"),
                    sizes = listOf(
                        ProductSize(sku = "1222-Bronzer-medium", current = 999.0, name = "Medium", inStock = 15, original = 1499.0, currency = "SEK"),
                    ),
                ),
            ),
        ),
        "1232" to HydratedProduct(
            sku = "1232",
            name = "Shine On Lip Gloss",
            brandName = "Shine",
            introduction = "Elevate your lip game with our high-shine, non-sticky lip gloss.",
            description = "Elevate your lip game with our high-shine, non-sticky lip gloss.",
            variations = listOf(
                Variation(
                    sku = "1232-gloss",
                    name = "Shine On Lip Gloss",
                    colorName = "gloss",
                    imageUrls = listOf("https://demo.bambuser.shop/wp-content/uploads/2023/05/Makeup_5.png"),
                    sizes = listOf(
                        ProductSize(sku = "614442-gloss-standard", current = 300.0, name = "Standard", inStock = 20, original = 999.0, currency = "SEK"),
                    ),
                ),
            ),
        ),
        "1223" to HydratedProduct(
            sku = "1223",
            name = "Lash Amplify Mascara",
            brandName = "Lash",
            introduction = "Efficient electric bike for urban commuting.",
            description = "Achieve voluminous, full lashes that command attention with our game-changing mascara.",
            variations = listOf(
                Variation(
                    sku = "1223-standard",
                    name = "Lash Amplify Mascara",
                    colorName = "default",
                    imageUrls = listOf("https://cdn.prod.website-files.com/66c31044a23e58e719bc6ffb/66c33c0b2e7fa3ab0d3b129d_Makeup_3.png"),
                    sizes = listOf(
                        ProductSize(sku = "1222-silver-standard", current = 800.0, name = "Standard", inStock = 20, original = 999.0, currency = "SEK"),
                    ),
                ),
            ),
        ),
        "1977" to HydratedProduct(
            sku = "1977",
            name = "Linen Blazer",
            brandName = "Bambuser",
            introduction = "Blazer",
            description = "Discover everyday elegance with our timeless Linen Blazer.",
            variations = listOf(
                Variation(
                    sku = "1223-small",
                    name = "Linen Blazer - Beige - Small",
                    colorName = "default",
                    imageUrls = listOf("https://demo.bambuser.shop/wp-content/uploads/2024/07/Blazer-Beige-1.png"),
                    sizes = listOf(
                        ProductSize(sku = "1222-beige-small", current = 5500.0, name = "Standard", inStock = 20, original = 5600.0, currency = "SEK"),
                    ),
                ),
            ),
        ),
        "1994" to HydratedProduct(
            sku = "1994",
            name = "Linen Pants",
            brandName = "Bambuser",
            introduction = "Pants",
            description = "Experience effortless sophistication with our classic Linen Pants.",
            variations = listOf(
                Variation(
                    sku = "1223-small",
                    name = "Linen Pants - Beige - Small",
                    colorName = "default",
                    imageUrls = listOf("https://demo.bambuser.shop/wp-content/uploads/2024/07/Pants-Beige.png"),
                    sizes = listOf(
                        ProductSize(sku = "1222-beige-small", current = 4000.0, name = "Standard", inStock = 20, original = 5000.0, currency = "SEK"),
                    ),
                ),
            ),
        ),
        "7777" to HydratedProduct(
            sku = "7777",
            name = "Bambuser Hoodie",
            brandName = "Bambuser",
            introduction = "A nice hoodie that keeps you warm",
            description = "World's best hoodie. Comes in all sizes and forms!",
            variations = listOf(
                Variation(
                    sku = "7777-black",
                    name = "Black Bambuser Hoodie",
                    colorName = "black",
                    imageUrls = listOf(
                        "https://demo.bambuser.shop/wp-content/uploads/2021/07/black-hoodie-front.png",
                        "https://demo.bambuser.shop/wp-content/uploads/2021/07/black-hoodie-right.jpeg",
                        "https://demo.bambuser.shop/wp-content/uploads/2021/07/black-hoodie-back.jpeg",
                        "https://demo.bambuser.shop/wp-content/uploads/2021/07/black-hoodie-left.jpeg",
                    ),
                    sizes = listOf(
                        ProductSize(sku = "7777-black-small", current = 120.0, name = "Small", inStock = 9, original = 120.0, currency = "USD"),
                        ProductSize(sku = "7777-black-xlarge", current = 100.0, name = "X-Large", inStock = 3, original = 120.0, currency = "USD"),
                    ),
                ),
                Variation(
                    sku = "7777-white",
                    name = "White Bambuser Hoodie",
                    colorName = "white",
                    imageUrls = listOf(
                        "https://demo.bambuser.shop/wp-content/uploads/2021/07/white-hoodie-front.png",
                        "https://demo.bambuser.shop/wp-content/uploads/2021/07/white-hoodie-right.jpeg",
                        "https://demo.bambuser.shop/wp-content/uploads/2021/07/white-hoodie-back.jpeg",
                        "https://demo.bambuser.shop/wp-content/uploads/2021/07/white-hoodie-left.jpeg",
                    ),
                    sizes = listOf(
                        ProductSize(sku = "7777-white-small", current = 100.0, name = "Small", inStock = 8, original = 120.0, currency = "USD"),
                        ProductSize(sku = "7777-white-xlarge", current = 100.0, name = "X-Large", inStock = 0, original = 120.0, currency = "USD"),
                    ),
                ),
                Variation(
                    sku = "7777-white-2",
                    name = "2x White Bambuser Hoodie",
                    colorName = "white",
                    imageUrls = listOf(
                        "https://demo.bambuser.shop/wp-content/uploads/2021/07/white-hoodie-front.png",
                        "https://demo.bambuser.shop/wp-content/uploads/2021/07/white-hoodie-right.jpeg",
                        "https://demo.bambuser.shop/wp-content/uploads/2021/07/white-hoodie-back.jpeg",
                        "https://demo.bambuser.shop/wp-content/uploads/2021/07/white-hoodie-left.jpeg",
                    ),
                    sizes = listOf(
                        ProductSize(sku = "7777-white-small-2", current = 180.0, name = "Small", inStock = 8, original = 239.0, currency = "USD"),
                        ProductSize(sku = "7777-white-xlarge-2", current = 180.0, name = "X-Large", inStock = 0, original = 239.0, currency = "USD"),
                    ),
                ),
            ),
        ),
    )

    fun findHydratedProduct(sku: String): HydratedProduct? {
        val parentSku = sku.split("-", limit = 2).firstOrNull() ?: sku
        return sampleProducts[parentSku] ?: findProductBySku(sku)
    }

    fun hydratedProduct(sku: String): HydratedProduct? = sampleProducts[sku]

    fun findProductBySku(sku: String): HydratedProduct? {
        for (product in sampleProducts.values) {
            for (variation in product.variations) {
                if (variation.sku == sku) return product
                for (size in variation.sizes) {
                    if (size.sku == sku) return product
                }
            }
        }
        return null
    }
}
