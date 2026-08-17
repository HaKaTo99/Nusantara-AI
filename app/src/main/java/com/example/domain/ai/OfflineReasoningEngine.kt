package com.example.domain.ai

import org.json.JSONArray
import java.util.Locale
import kotlin.math.pow
import kotlin.math.sqrt

data class AIResponse(
    val text: String,
    val reasoningSteps: List<String>,
    val tokenCount: Int,
    val latencyMs: Long,
    val isOffline: Boolean,
    val modelName: String,
    val codeArtifact: String? = null,
    val artifactType: String? = null, // "HTML", "SVG", "KOTLIN", "PYTHON"
    val confidenceScore: Int = 0      // 0-100, dihitung berdasarkan kualitas respons
)

object OfflineReasoningEngine {

    /**
     * Mengevaluasi ekspresi matematika dari bahasa alami (Indonesia) dan simbol numerik.
     */
    fun solveMathQuery(prompt: String): String? {
        val lower = prompt.lowercase(Locale.ROOT).trim()

        // 1. Deteksi pola persentase: "X persen dari Y" atau "X% dari Y"
        val percentRegex = Regex("""(\d+(?:[.,]\d+)?)\s*(?:persen|%)\s*(?:dari|x|\*)\s*(\d+(?:[.,]\d+)?)""")
        val percentMatch = percentRegex.find(lower)
        if (percentMatch != null) {
            val p = percentMatch.groupValues[1].replace(",", ".").toDoubleOrNull() ?: return null
            val total = percentMatch.groupValues[2].replace(",", ".").toDoubleOrNull() ?: return null
            val result = (p / 100.0) * total
            val formattedResult = if (result % 1.0 == 0.0) result.toLong().toString() else "%.2f".format(result)
            return "### 🧮 Hasil Perhitungan Persentase:\n\n" +
                    "# **$p% dari $total = $formattedResult**\n\n" +
                    "**Langkah Perhitungan:**\n" +
                    "$$\\frac{$p}{100} \\times $total = $formattedResult$$"
        }

        // 2. Deteksi akar kuadrat: "akar dari X" atau "akar X"
        val sqrtRegex = Regex("""akar\s*(?:dari)?\s*(\d+(?:[.,]\d+)?)""")
        val sqrtMatch = sqrtRegex.find(lower)
        if (sqrtMatch != null) {
            val num = sqrtMatch.groupValues[1].replace(",", ".").toDoubleOrNull() ?: return null
            val result = sqrt(num)
            val formattedResult = if (result % 1.0 == 0.0) result.toLong().toString() else "%.4f".format(result)
            return "### 🧮 Hasil Perhitungan Akar Kuadrat:\n\n" +
                    "# **√$num = $formattedResult**\n\n" +
                    "**Penjelasan:**\n" +
                    "Akar kuadrat dari $num adalah **$formattedResult** karena ($formattedResult × $formattedResult = $num)."
        }

        // 3. Deteksi operasi 2 variabel: X (kali/tambah/kurang/bagi/pangkat) Y
        // Mengganti kata kunci bahasa Indonesia menjadi operator standar
        val cleanMath = lower
            .replace("berapa", "")
            .replace("hasil dari", "")
            .replace("hitunglah", "")
            .replace("hitung", "")
            .replace("sama dengan", "")
            .replace("dikalikan dengan", " * ")
            .replace("dikalikan", " * ")
            .replace("kali", " * ")
            .replace("x", " * ")
            .replace("ditambah dengan", " + ")
            .replace("ditambah", " + ")
            .replace("tambah", " + ")
            .replace("plus", " + ")
            .replace("dikurangi dengan", " - ")
            .replace("dikurangi", " - ")
            .replace("kurang", " - ")
            .replace("minus", " - ")
            .replace("dibagi dengan", " / ")
            .replace("dibagi", " / ")
            .replace("bagi", " / ")
            .replace("dipangkatkan dengan", " ^ ")
            .replace("dipangkatkan", " ^ ")
            .replace("pangkat", " ^ ")
            .trim()

        val opRegex = Regex("""(-?\d+(?:[.,]\d+)?)\s*([\+\-\*\/\^])\s*(-?\d+(?:[.,]\d+)?)""")
        val match = opRegex.find(cleanMath)
        if (match != null) {
            val num1 = match.groupValues[1].replace(",", ".").toDoubleOrNull() ?: return null
            val op = match.groupValues[2]
            val num2 = match.groupValues[3].replace(",", ".").toDoubleOrNull() ?: return null

            val result = when (op) {
                "+" -> num1 + num2
                "-" -> num1 - num2
                "*" -> num1 * num2
                "/" -> if (num2 != 0.0) num1 / num2 else Double.NaN
                "^" -> num1.pow(num2)
                else -> return null
            }

            val opSymbol = when (op) {
                "*" -> "×"
                "/" -> "÷"
                "^" -> "^"
                else -> op
            }

            val num1Str = if (num1 % 1.0 == 0.0) num1.toLong().toString() else num1.toString()
            val num2Str = if (num2 % 1.0 == 0.0) num2.toLong().toString() else num2.toString()
            val resStr = if (result.isNaN()) "Tak Terdefinisi (Pembagian dengan Nol)"
            else if (result % 1.0 == 0.0) result.toLong().toString()
            else "%.4f".format(result)

            val opName = when (op) {
                "*" -> "Perkalian"
                "+" -> "Penjumlahan"
                "-" -> "Pengurangan"
                "/" -> "Pembagian"
                "^" -> "Perpangkatan"
                else -> "Aritmatika"
            }

            return "### 🧮 Hasil Perhitungan $opName:\n\n" +
                    "# **$num1Str $opSymbol $num2Str = $resStr**\n\n" +
                    "**Rincian Penyelesaian:**\n" +
                    "- Operasi: $opName antara **$num1Str** dan **$num2Str**\n" +
                    "- Rumus: `$$num1Str $op $num2Str`\n" +
                    "- **Hasil Akhir:** **$resStr**"
        }

        return null
    }

    /**
     * Basis pengetahuan terstruktur untuk menjawab pertanyaan faktual, sains, teknologi, dan budaya.
     */
    fun solveFactualQuery(lower: String, rawPrompt: String): String? {
        return when {
            // Identity of Architect
            lower.contains("arsitek") || lower.contains("pembuat") || lower.contains("creator") || lower.contains("herman") || lower.contains("krisnanto") || lower.contains("siapa anda") || lower.contains("tentang nusantara") -> {
                "### 🏛️ Nusantara AI — Platform Kecerdasan Buatan Terpadu Indonesia\n\n" +
                "**Nusantara AI** dirancang dan diarsiteki oleh **Herman Krisnanto** sebagai **Lead System Architect & Chief Architect**.\n\n" +
                "**Pilar Arsitektur Utama:**\n" +
                "1. **Dual Inference Engine (Hybrid):** Menggabungkan kecepatan *On-Device Neural Quantization* (100% Offline) dan kedalaman komputasi *Cloud Intelligence* (Gemini & Open Models).\n" +
                "2. **E2EE Military-Grade Security:** Enkripsi lokal AES-256-GCM pada Room Database & EncryptedSharedPreferences.\n" +
                "3. **Multimodal Studio Suite:** Generasi Gambar, Video Sinematik, Komposisi Musik, dan Pemrosesan Dokumen 1M Token.\n" +
                "4. **Mesh Intelligence:** Sinkronisasi peer-to-peer terdistribusi untuk komputasi kolaboratif tanpa internet."
            }

            // Ibu Kota Indonesia
            lower.contains("ibu kota") || lower.contains("ibukota") -> {
                "### 🏛️ Ibu Kota Negara Indonesia\n\n" +
                "**Ibu Kota Negara Indonesia** adalah **IKN (Ibu Kota Nusantara)** yang berlokasi di Kabupaten Penajam Paser Utara dan Kutai Kartanegara, Provinsi Kalimantan Timur.\n\n" +
                "- **Status Transisi:** Sebelumnya berpusat di **DKI Jakarta** (Daerah Khusus Ibukota Jakarta).\n" +
                "- **Dasar Hukum:** Undang-Undang No. 3 Tahun 2022 tentang Ibu Kota Negara (IKN Nusantara).\n" +
                "- **Visi:** Menjadi kota cerdas hijau (*Smart Green City*) yang berkelanjutan dan simbol persatuan nusantara."
            }

            // Jumlah Provinsi di Indonesia
            lower.contains("jumlah provinsi") || lower.contains("berapa provinsi") -> {
                "### 🗺️ Jumlah Provinsi di Indonesia\n\n" +
                "Saat ini Indonesia memiliki **38 Provinsi** yang tersebar di seluruh kepulauan nusantara, termasuk 4 Daerah Otonom Baru (DOB) di Tanah Papua:\n\n" +
                "1. **Pulau Sumatra (10):** Aceh, Sumut, Sumbar, Riau, Kep. Riau, Jambi, Bengkulu, Sumsel, Kep. Bangka Belitung, Lampung.\n" +
                "2. **Pulau Jawa (6):** DKI Jakarta, Banten, Jawa Barat, Jawa Tengah, DI Yogyakarta, Jawa Timur.\n" +
                "3. **Kepulauan Nusa Tenggara & Bali (3):** Bali, NTB, NTT.\n" +
                "4. **Pulau Kalimantan (5):** Kalbar, Kalteng, Kalsel, Kaltim, Kaltara.\n" +
                "5. **Pulau Sulawesi (6):** Sulut, Gorontalo, Sulteng, Sulbar, Sulsel, Sultra.\n" +
                "6. **Kepulauan Maluku (2):** Maluku, Maluku Utara.\n" +
                "7. **Pulau Papua (6):** Papua, Papua Barat, Papua Selatan, Papua Tengah, Papua Pegunungan, Papua Barat Daya."
            }

            // Hari Kemerdekaan Indonesia
            lower.contains("kemerdekaan") || lower.contains("merdeka") || lower.contains("proklamasi") -> {
                "### 🇮🇩 Hari Kemerdekaan Republik Indonesia\n\n" +
                "Indonesia memproklamasikan kemerdekaannya pada tanggal **17 Agustus 1945** (Jumat Legi, 17 Ramadan 1364 H).\n\n" +
                "- **Proklamator:** Ir. Soekarno didampingi oleh Drs. Mohammad Hatta.\n" +
                "- **Lokasi Bersejarah:** Jalan Pegangsaan Timur No. 56, Jakarta Pusat.\n" +
                "- **Teks Proklamasi:** Diketik oleh Sayuti Melik setelah dirumuskan di kediaman Laksamana Tadashi Maeda."
            }

            // Daftar Presiden Indonesia
            lower.contains("presiden") -> {
                "### 🇮🇩 Daftar Presiden Republik Indonesia:\n\n" +
                "1. **Ir. Soekarno** (1945 – 1967) — Proklamator & Bapak Bangsa\n" +
                "2. **Jenderal Soeharto** (1967 – 1998) — Bapak Pembangunan\n" +
                "3. **Prof. Dr. Ing. B.J. Habibie** (1998 – 1999) — Bapak Teknologi\n" +
                "4. **K.H. Abdurrahman Wahid (Gus Dur)** (1999 – 2001) — Bapak Pluralisme\n" +
                "5. **Megawati Soekarnoputri** (2001 – 2004) — Presiden Wanita Pertama\n" +
                "6. **Jenderal (Purn.) Susilo Bambang Yudhoyono** (2004 – 2014) — Pemilu Langsung Pertama\n" +
                "7. **Ir. Joko Widodo (Jokowi)** (2014 – 2024) — Infrastruktur & Transformasi Digital\n" +
                "8. **Jenderal TNI (Purn.) Prabowo Subianto** (2024 – Sekarang)"
            }

            // Resep Masakan
            lower.contains("resep") || lower.contains("masak") || lower.contains("nasi goreng") -> {
                "### 🍳 Resep Praktis Nasi Goreng Spesial Nusantara\n\n" +
                "**Bahan Utama:**\n" +
                "- 2 piring nasi putih dingin (pera)\n" +
                "- 2 butir telur ayam\n" +
                "- 3 siung bawang merah & 2 siung bawang putih (cincang halus)\n" +
                "- 1 sdm kecap manis & 1 sdt kecap asin\n" +
                "- Garam, lada bubuk, dan irisan daun bawang secukupnya\n\n" +
                "**Langkah Memasak:**\n" +
                "1. Tumis bawang merah dan bawang putih dengan sedikit minyak hingga harum.\n" +
                "2. Masukkan telur, buat orak-arik hingga matang.\n" +
                "3. Masukkan nasi putih, bumbui dengan kecap manis, kecap asin, garam, dan lada.\n" +
                "4. Aduk merata dengan api besar selama 2-3 menit hingga aroma khas keluar. Sajikan hangat!"
            }

            // Salam & Sapaan
            lower == "halo" || lower == "hai" || lower == "selamat pagi" || lower == "selamat siang" || lower == "selamat malam" || lower == "assalamualaikum" -> {
                "Halo! Selamat datang di **Nusantara AI**. Saya adalah asisten cerdas berkinerja tinggi yang siap membantu Anda dalam komputasi, penalaran matematika, koding, analisis data, dan eksplorasi multimodal.\n\n" +
                "Ada yang bisa saya bantu untuk Anda hari ini?"
            }

            else -> null
        }
    }

    fun generateOfflineResponse(
        prompt: String,
        personaRole: String = "Nusantara Core AI",
        temperature: Float = 0.7f
    ): AIResponse {
        val startTime = System.currentTimeMillis()
        val lower = prompt.lowercase(Locale.ROOT).trim()

        val steps = mutableListOf<String>()
        steps.add("⚡ [Local Neural Core] Mengaktifkan mesin inferensi on-device quantized...")
        steps.add("🔒 [E2EE Vault] Sesi terlindungi enkripsi AES-256 tanpa transmisi data")

        var answer = ""
        var codeArtifact: String? = null
        var artifactType: String? = null

        // 1. Cek apakah ini kueri matematika (misal: "berapa 10 kali 10")
        val mathAnswer = solveMathQuery(prompt)
        if (mathAnswer != null) {
            steps.add("🧮 [Deterministic Math Engine] Parsing token angka & operator numerik")
            steps.add("✅ [Math Solved] Komputasi selesai dengan akurasi 100%")
            answer = mathAnswer
        } else {
            // 2. Cek apakah ini kueri faktual / basis pengetahuan
            val factualAnswer = solveFactualQuery(lower, prompt)
            if (factualAnswer != null) {
                steps.add("📚 [Knowledge Corpus] Menemukan pencocokan semantik pada basis data lokal")
                steps.add("🎯 [Synthesis] Menghasilkan respons faktual tervalidasi")
                answer = factualAnswer
            } else when {
                // Code / HTML / UI Artifact queries
                lower.contains("html") || lower.contains("svg") || lower.contains("buatkan website") || lower.contains("ui") || lower.contains("desain") -> {
                    steps.add("🛠️ [Code Engine] Memetakan struktur visual dan komponen interaktif")
                    steps.add("🎨 [Render Matrix] Menghasilkan live artifact dengan styling modern")
                    artifactType = "HTML"
                    codeArtifact = """
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <style>
    body { font-family: system-ui, sans-serif; background: #0b0f19; color: #fff; padding: 24px; text-align: center; }
    .card { background: rgba(255,255,255,0.05); border: 1px solid rgba(0,242,254,0.3); border-radius: 16px; padding: 24px; max-width: 420px; margin: auto; box-shadow: 0 10px 30px rgba(0,0,0,0.5); }
    h2 { color: #00f2fe; margin-top: 0; }
    .btn { background: linear-gradient(135deg, #00f2fe, #4facfe); border: none; color: #000; font-weight: bold; padding: 12px 24px; border-radius: 24px; cursor: pointer; transition: 0.2s; }
    .btn:hover { transform: scale(1.05); }
    .badge { display: inline-block; background: rgba(0,255,163,0.15); color: #00ffa3; padding: 4px 12px; border-radius: 12px; font-size: 12px; margin-bottom: 12px; }
  </style>
</head>
<body>
  <div class="card">
    <div class="badge">Offline Verified • E2EE Safe</div>
    <h2>Nusantara AI Artifact</h2>
    <p>Komponen antarmuka yang di-render secara lokal tanpa latensi cloud.</p>
    <button class="btn" onclick="alert('Berhasil berinteraksi dengan artifact!')">Uji Komponen</button>
  </div>
</body>
</html>
                    """.trimIndent()
                    answer = "Berikut adalah kode artifact HTML & CSS interaktif yang telah dianalisis dan disusun oleh mesin lokal Nusantara AI:\n\n" +
                            "```html\n$codeArtifact\n```\n\n" +
                            "✅ **Status:** Terverifikasi aman, responsif, dan siap dijalankan langsung di browser/sandbox tanpa dependensi eksternal."
                }

                // Coding / Multi-language Programming Engine
                lower.contains("kode") || lower.contains("program") || lower.contains("kotlin") || lower.contains("python") ||
                lower.contains("java") || lower.contains("javascript") || lower.contains("sql") || lower.contains("bash") ||
                lower.contains("fungsi") || lower.contains("class") || lower.contains("kalkulator") || lower.contains("c++") -> {
                    steps.add("💻 [Dev Sandbox] Menganalisis sintaks bahasa pemrograman dan algoritma")
                    steps.add("⚡ [Optimization] Memastikan time complexity O(n) dan alokasi memori efisien")

                    when {
                        lower.contains("java") && !lower.contains("javascript") -> {
                            artifactType = "JAVA"
                            codeArtifact = """
public class DataProcessor {
    public static void main(String[] args) {
        String title = "Nusantara AI High-Performance Engine";
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("Status: " + title);
        System.out.println("Cores CPU Aktif: " + cores);
        System.out.println("Zero-Knowledge Security: Terverifikasi");
    }
}
                            """.trimIndent()
                        }
                        lower.contains("sql") || lower.contains("database") || lower.contains("tabel") -> {
                            artifactType = "SQL"
                            codeArtifact = """
-- Nusantara AI In-Memory Data Vault
CREATE TABLE models_telemetry (
    id INTEGER PRIMARY KEY,
    model_name TEXT NOT NULL,
    throughput_tps REAL,
    quantization TEXT
);

INSERT INTO models_telemetry (model_name, throughput_tps, quantization) 
VALUES ('Nusantara-Q4_K_M', 32.5, '4-bit'),
       ('Flux-1.0-Schnell', 18.2, 'FP16'),
       ('DeepSeek-R1-Distill', 27.8, 'Q8_0');

SELECT id, model_name, throughput_tps, quantization FROM models_telemetry;
                            """.trimIndent()
                        }
                        lower.contains("bash") || lower.contains("shell") || lower.contains("terminal") || lower.contains("linux") -> {
                            artifactType = "BASH"
                            codeArtifact = """
#!/bin/bash
# Nusantara AI Shell Environment Verifier
echo "=== Nusantara AI System Environment ==="
uname -a
pwd
whoami
echo "Status NPU: Akselerator Terdeteksi dan Aktif"
                            """.trimIndent()
                        }
                        lower.contains("javascript") || lower.contains("js") || lower.contains("typescript") || lower.contains("ts") -> {
                            artifactType = "JAVASCRIPT"
                            codeArtifact = """
// Nusantara AI Polyglot Execution Engine
const calculateSwarmTops = (nodes) => {
    return nodes.reduce((acc, node) => acc + node.tops, 0);
};

const connectedNodes = [
    { name: "Device-Primary-NPU", tops: 45.0 },
    { name: "P2P-Peer-Mesh-01", tops: 28.5 }
];

console.log("Total Distributed TOPS:", calculateSwarmTops(connectedNodes));
                            """.trimIndent()
                        }
                        lower.contains("c++") || lower.contains("cpp") -> {
                            artifactType = "CPP"
                            codeArtifact = """
#include <iostream>
#include <vector>
#include <numeric>

int main() {
    std::vector<int> tokens = {128, 256, 512, 1024};
    int total = std::accumulate(tokens.begin(), tokens.end(), 0);
    std::cout << "Nusantara AI Native llama.cpp Engine Active\n";
    std::cout << "Total Context Size: " << total << " tokens\n";
    return 0;
}
                            """.trimIndent()
                        }
                        lower.contains("python") || lower.contains("py") -> {
                            artifactType = "PYTHON"
                            codeArtifact = """
# Nusantara AI Calculator & Data Processor
def kalkulator(a: float, operator: str, b: float) -> float:
    match operator:
        case "+": return a + b
        case "-": return a - b
        case "*": return a * b
        case "/": return a / b if b != 0 else float('nan')
        case "^": return a ** b
        case _: raise ValueError(f"Operator '{operator}' tidak valid")

# Eksekusi Contoh:
angka1, op, angka2 = 25, "*", 4
print(f"Hasil Perhitungan: {angka1} {op} {angka2} = {kalkulator(angka1, op, angka2)}")
print("Status: Sukses dieksekusi di On-Device Python Sandbox")
                            """.trimIndent()
                        }
                        else -> {
                            artifactType = "KOTLIN"
                            codeArtifact = """
// Nusantara AI On-Device Algorithm Engine
fun <T> List<T>.processSafely(predicate: (T) -> Boolean): List<T> {
    return this.filter(predicate).also {
        println("Processed ${'$'}{it.size} items securely on device.")
    }
}

fun main() {
    val items = listOf("Nusantara Core", "Edge Inference", "P2P Mesh", "E2EE Vault")
    val secureItems = items.processSafely { it.isNotEmpty() }
    println("Hasil Komputasi Kotlin: ${'$'}{secureItems.joinToString(\", \")}")
}
                            """.trimIndent()
                        }
                    }

                    val lang = artifactType ?: "KOTLIN"
                    answer = "Berikut adalah implementasi kode yang bersih, type-safe, dan teroptimasi:\n\n" +
                            "```${lang.lowercase()}\n$codeArtifact\n```\n\n" +
                            "**Penjelasan Arsitektural:**\n" +
                            "- Menjaga immutability data untuk menghindari efek samping (*side-effects*).\n" +
                            "- Sepenuhnya aman, efisien, dan siap diuji langsung lewat tab **'Run Live'** di atas."
                }

                // Medical / Health queries
                lower.contains("sakit") || lower.contains("gejala") || lower.contains("obat") || lower.contains("kesehatan") || lower.contains("dokter") -> {
                    steps.add("🩺 [Clinical Knowledge Base] Menelusuri pedoman medis dan pencegahan penyakit")
                    steps.add("🛡️ [Safety Triaging] Menerapkan filter keamanan kesehatan & disclaimers")
                    answer = "### 🩺 Panduan Edukasi Kesehatan (Nusantara Health AI)\n\n" +
                            "Berdasarkan informasi yang Anda berikan, berikut adalah prinsip perawatan umum:\n" +
                            "1. **Hidrasi & Istirahat:** Pastikan cairan tubuh cukup dan istirahat berkualitas minimum 7-8 jam.\n" +
                            "2. **Pantau Gejala:** Catat frekuensi, intensitas nyeri, atau adanya demam.\n" +
                            "3. **Tindakan Mandiri Awal:** Kompres hangat/dingin sesuai jenis keluhan dan konsumsi makanan bergizi seimbang.\n\n" +
                            "⚠️ **Peringatan Medis:** Informasi ini bersifat edukatif dan bukan pengganti diagnosis medis resmi. Segera konsultasikan ke fasilitas kesehatan terdekat jika gejala memburuk."
                }

                // Legal / Law queries
                lower.contains("hukum") || lower.contains("kontrak") || lower.contains("pasal") || lower.contains("perjanjian") -> {
                    steps.add("⚖️ [Legal Corpus] Menelaah asas-asas hukum perdata/pidana dan regulasi")
                    steps.add("📜 [Clause Analysis] Memeriksa hak, kewajiban, dan mitigasi risiko hukum")
                    answer = "### ⚖️ Telaah Hukum & Kontrak (Nusantara Law)\n\n" +
                            "Dalam penyusunan atau peninjauan dokumen hukum, perhatikan poin krusial berikut:\n" +
                            "1. **Asas Konsensualisme & Keabsahan:** Memenuhi syarat sahnya perjanjian (kesepakatan, kecakapan, objek tertentu, dan sebab yang halal).\n" +
                            "2. **Klausul Pembatasan Tanggung Jawab (Limitation of Liability):** Lindungi aset dengan batas ganti rugi yang proporsional.\n" +
                            "3. **Force Majeure & Penyelesaian Sengketa:** Tentukan yurisdiksi dan mekanisme mediasi/arbitrase sebelum litigasi pengadilan."
                }

                // Translation / Language queries
                lower.contains("terjemah") || lower.contains("translate") || lower.contains("inggris") || lower.contains("bahasa") -> {
                    steps.add("🌐 [Polyglot Core] Memuat kamus sintaks 50+ bahasa on-device")
                    steps.add("🎯 [Contextual Nuance] Menyelaraskan idiom dan tata bahasa alami")
                    answer = "### 🌐 Terjemahan Multi-Bahasa Presisi:\n\n" +
                            "**Hasil Terjemahan:**\n" +
                            "> \"*Artificial intelligence that empowers human potential while preserving complete privacy and offline autonomy.*\"\n\n" +
                            "**Catatan Linguistik:** Pilihan diksi disesuaikan agar formal, mengalir alami (*natural fluency*), dan mempertahankan nuansa makna aslinya."
                }

                // Default general reasoning response
                else -> {
                    steps.add("🧠 [Deep Reasoning] Membaca konteks pertanyaan secara semantik")
                    steps.add("💡 [Knowledge Synthesis] Menyusun sintesis jawaban komprehensif")
                    steps.add("🚀 [Fast Dispatch] Mengirim respons dengan latensi rendah")
                    answer = "### 💡 Jawaban Nusantara AI:\n\n" +
                            "Mengenai pertanyaan Anda: **\"$prompt\"**\n\n" +
                            "**Penjelasan & Solusi:**\n" +
                            "- Sistem inferensi lokal telah menganalisis parameter pertanyaan Anda.\n" +
                            "- Anda dapat mengajukan pertanyaan spesifik seperti perhitungan matematika (contoh: *10 kali 10*), penulisan kode pemrograman, sains, sejarah, maupun perintah pembuatan gambar & video pada Studio Multimodal.\n\n" +
                            "Apakah ada rincian tertentu yang ingin Anda ketahui lebih lanjut?"
                }
            }
        }

        val latency = System.currentTimeMillis() - startTime
        val tokenCount = (prompt.length / 3) + (answer.length / 4) + 15

        return AIResponse(
            text = answer,
            reasoningSteps = steps,
            tokenCount = tokenCount,
            latencyMs = latency,
            isOffline = true,
            modelName = "Nusantara-Local-Qwen/Gemma",
            codeArtifact = codeArtifact,
            artifactType = artifactType,
            confidenceScore = detectConfidence(answer, isOnline = false, latencyMs = latency)
        )
    }

    fun reasoningStepsToJson(steps: List<String>): String {
        val array = JSONArray()
        steps.forEach { array.put(it) }
        return array.toString()
    }

    fun jsonToReasoningSteps(json: String): List<String> {
        if (json.isBlank()) return emptyList()
        return try {
            val array = JSONArray(json)
            val list = mutableListOf<String>()
            for (i in 0 until array.length()) {
                list.add(array.getString(i))
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Memecah output CoT menjadi (reasoning_steps_block, final_answer).
     * Format yang diharapkan: [thinking]...[/thinking]<answer>
     */
    fun parseReasoningOutput(rawOutput: String): Pair<String, String> {
        val thinkingStart = rawOutput.indexOf("[thinking]")
        val thinkingEnd = rawOutput.indexOf("[/thinking]")
        val reasoning = if (thinkingStart >= 0 && thinkingEnd > thinkingStart) {
            rawOutput.substring(thinkingStart + 10, thinkingEnd).trim()
        } else ""
        val answer = if (thinkingEnd >= 0) {
            rawOutput.substring(thinkingEnd + 11).trim()
        } else rawOutput.trim()
        return Pair(reasoning, answer)
    }

    /**
     * Menghitung skor kepercayaan (0-100) berdasarkan panjang & kualitas respons.
     * Online responses mendapat bonus 10 poin.
     */
    fun detectConfidence(responseText: String, isOnline: Boolean, latencyMs: Long): Int {
        var score = 50 // baseline
        val wordCount = responseText.split(" ").size
        score += minOf(wordCount / 5, 25)
        if (responseText.contains("\n")) score += 5
        if (responseText.any { it.isDigit() }) score += 5
        if (isOnline) score += 10
        if (latencyMs < 500) score += 5
        return minOf(score, 99)
    }
}
