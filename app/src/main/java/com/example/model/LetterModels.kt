package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*
import java.util.UUID

enum class HeaderStyle(val displayName: String, val description: String) {
    ROMANTIC_PERSONAL("Heartfelt & Personal", "Warm handwritten greeting, romantic header flourish"),
    CORPORATE_OFFICIAL("Executive Letterhead", "Modern corporate header, 2-column contact grid, reference no."),
    MINIMAL_MODERN("Minimalist Monogram", "Crisp monogram badge, sleek divider lines"),
    VINTAGE_CREST("Royal Vintage Crest", "Classic laurel crest, ornamental antique flourishes"),
    ARTISAN_BOTANICAL("Botanical Studio", "Delicate floral sprigs, artisan aesthetic")
}

enum class PaperTheme(val displayName: String, val paperColor: Color, val lineColor: Color) {
    CREAM_LAID("Cream Laid", JpPaperCream, Color(0x1C33314E)),
    VINTAGE_PARCHMENT("Vintage Parchment", JpPaperVintage, Color(0x22735738)),
    BLUSH_ROSE("Blush Rose", JpPaperBlush, Color(0x22F06E9A)),
    SAGE_MINT("Sage Mint", JpPaperSage, Color(0x203D6643)),
    SKY_PASTEL("Sky Pastel", JpPaperSky, Color(0x2034567A)),
    CRISP_WHITE("Crisp White", JpPaperCrisp, Color(0x1533314E))
}

enum class FontChoice(val displayName: String, val previewName: String) {
    HANDWRITTEN("Handwritten Script", "Cursive & intimate"),
    SERIF_CLASSIC("Classic Serif", "Literary & formal"),
    MONO_TYPEWRITER("Vintage Typewriter", "Nostalgic monospace"),
    SANS_CLEAN("Modern Sans", "Clean & contemporary")
}

enum class SealStyle(val displayName: String) {
    HEART("Wax Heart"),
    BOW("Ribbon Bow"),
    LILY("Blooming Lily"),
    TEDDY("Teddy Bear"),
    STAR("Twinkle Star"),
    ROSE_CREST("Rose Crest"),
    MONOGRAM("Monogram Initial")
}

enum class SealColor(val displayName: String, val color: Color, val hexString: String) {
    CRIMSON("Crimson Red", JpSealRed, "#C1392B"),
    PETAL_ROSE("Petal Rose", JpSealRose, "#F4A4C0"),
    TEDDY_CARAMEL("Teddy Caramel", JpSealTeddy, "#A9744F"),
    ROYAL_NAVY("Royal Navy", JpSealNavy, "#2B3A67"),
    SAGE_OLIVE("Sage Olive", JpSealSage, "#557B55"),
    ANTIQUE_GOLD("Antique Gold", JpSealGold, "#D4A340"),
    DEEP_INK("Midnight Ink", JpInk, "#33314E")
}

enum class StickerType(val label: String, val emoji: String) {
    POSTAGE_STAMP("Air Mail Stamp", "📮"),
    BOW("Silk Bow", "🎀"),
    LILY("Lily Flower", "🌸"),
    WAX_HEART("Heart Seal", "❤️"),
    COFFEE("Warm Cup", "☕"),
    STAR("Golden Star", "⭐"),
    TEDDY("Teddy", "🧸"),
    BOTANICAL("Pressed Fern", "🌿"),
    WASHI_TAPE("Washi Tape", "🏷️")
}

data class LetterSticker(
    val id: String = UUID.randomUUID().toString(),
    val type: StickerType,
    val posX: Float = 0.5f, // 0..1 relative on paper
    val posY: Float = 0.5f,
    val rotation: Float = 0f,
    val scale: Float = 1.0f
)

data class LetterData(
    val recipient: String = "Sophie",
    val recipientTitle: String = "Design Director",
    val recipientCompany: String = "Atelier de Lumière",
    val recipientAddress: String = "12 Boulevard Saint-Germain, Paris",
    val salutation: String = "My dearest Sophie,",
    
    val sender: String = "Sabi",
    val senderTitle: String = "Creative Director",
    val senderOrg: String = "Janapa Studio",
    val senderAddress: String = "44 Wisteria Walk, Studio 3B",
    val senderEmail: String = "sabi@janapasabi.com",
    val senderPhone: String = "+1 (555) 392-8104",
    val senderWebsite: String = "letter.janapasabi.com",
    val referenceNumber: String = "JS/2026/089-A",
    val date: String = "October 3, 2026",
    
    val subject: String = "Some words deserve to be written down",
    val bodyText: String = """I wanted to send you something you could hold in your hands.

In a world of fleeting notifications, take a breath and know how deeply appreciated you are. Every idea we spoke about has begun to flourish, and every sunrise feels a little warmer remembering our shared laughter.

May this little letter find you surrounded by quiet peace, a warm cup in hand, and the gentle reminder that great things take time to unfold.""",
    
    val signOff: String = "With all my love and fondness,",
    val signatureText: String = "Sabi",
    val initialMonogram: String = "S",
    
    val headerStyle: HeaderStyle = HeaderStyle.ROMANTIC_PERSONAL,
    val paperTheme: PaperTheme = PaperTheme.CREAM_LAID,
    val fontChoice: FontChoice = FontChoice.HANDWRITTEN,
    val sealStyle: SealStyle = SealStyle.HEART,
    val sealColor: SealColor = SealColor.CRIMSON,
    val showRuledLines: Boolean = true,
    
    val stickers: List<LetterSticker> = listOf(
        LetterSticker(
            id = "stamp-1",
            type = StickerType.POSTAGE_STAMP,
            posX = 0.84f,
            posY = 0.08f,
            rotation = -4f,
            scale = 1.0f
        ),
        LetterSticker(
            id = "lily-1",
            type = StickerType.LILY,
            posX = 0.12f,
            posY = 0.90f,
            rotation = 8f,
            scale = 1.1f
        )
    ),
    
    val hasPolaroid: Boolean = false,
    val polaroidCaption: String = "autumn in the park",
    val polaroidRotation: Float = -3f
)
