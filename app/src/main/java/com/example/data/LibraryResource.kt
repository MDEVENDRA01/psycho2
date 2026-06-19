package com.example.data

data class LibraryResource(
    val id: String,
    val category: String,
    val title: String,
    val description: String,
    val duration: String,
    val type: String, // "Listen Now", "Read Article", "Start Practice"
    val imageUrl: String,
    val iconName: String,
    val content: String,
    val exerciseType: String? = null // "breath", "audio", "article"
)

object LibraryData {
    val items = listOf(
        LibraryResource(
            id = "centering",
            category = "Mindfulness",
            title = "Morning Centering Practice",
            description = "Start your day with gentle awareness. This guided audio helps ground your thoughts before the day begins.",
            duration = "10 min audio",
            type = "Listen Now",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuB6rNnMMnc_q8HYTTeBFml9ENV_Ihk-mXsY9T9xHGAOOpk37jc3XWeVPcAhdofT4EnDTlWEVQ76QtkYWH3CLekEOoZ9vi5NY2vpThakSVsWG172ckAf8uutgbNCJXWu9yU_oBGqSXOAoUuhiTghcyhh2oS1nNL5S3Tf66tUXqt8oiSi2dwA_FfOI1nERRy-nCO_X7j8m4nYZ-CvaqXc_WHQetM7TFRpmA88txLmnsXn6S8C8ZEd-mTX",
            iconName = "headphones",
            exerciseType = "audio",
            content = """
                Welcome to your Morning Centering Practice. 
                
                Find a comfortable seated position. Rest your hands gently on your lap. Feel the weight of your body pressing into the seat, supported fully by the ground.
                
                Slowly close your eyes, or cast a soft glance downward...
                
                Let's begin by taking a deep, conscious breath in... fill your chest and hold it for a moment. And now, release, letting any residual sleepiness or tension flow away.
                
                Inhale deeply once more, absorbing clarity. Exhale softly, releasing expectations for the day ahead.
                
                Observe the rise and fall of your shoulders. If any thoughts pass through your mind, acknowledge them like fluffy clouds drifting across a vast sky, and return your attention back to your breath.
                
                You are here. You are present. You are ready.
            """.trimIndent()
        ),
        LibraryResource(
            id = "grounding",
            category = "Anxiety Relief",
            title = "The 5-4-3-2-1 Grounding Technique",
            description = "A simple sensory exercise to bring you back to the present moment when feeling overwhelmed.",
            duration = "5 min read",
            type = "Read Article",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuD1Y7ALyJU_rNDiYhrgOKQlULH_6N9pIHnF3n0HpKd1Dy3dwvfpHd2PAIrbEI5WnkQgs8JI58hfTDjlJnXZrkENbOmXExRhc1g89P-hBqLXYYtkYttD913LGML0Djt2kdMVDPCZPzCknf-IQTrKoj_yaX7XGvq2GyZD7Kh_UqA_nYQKcfdvYr-HmNboqpQM181PfVDR_jkHeXOJxWJpgk1G5Py1_uVc32nH5mAwBSIAtGwRjtXN9w8s",
            iconName = "menu_book",
            exerciseType = "article",
            content = """
                When anxiety rushes in, your nervous system is flooded with "flight-or-fight" alarms. Grounding techniques act as circuit breakers, diverting your attention away from racing thoughts and routing it back into your primary physical senses.
                
                The 5-4-3-2-1 method is a famous mindfulness tool:
                
                👀 5 THINGS YOU CAN SEE:
                Look around you. Identify five distinct objects. A crack in the wall, a blue pencil, a plant on the windowsill, or a distant light. Pay raw attention to their shapes and colors.
                
                👉 4 THINGS YOU CAN TOUCH:
                Examine your immediate tactile world. Feel four unique sensations. The cold fabric of your jeans, the smooth oak of the desk, the rough fibers of a carpet, or the cool air breezing against your hands.
                
                👂 3 THINGS YOU CAN HEAR:
                Listen intently. Name three sounds running in the background. The low hum of the refrigerator, distant car tires rolling on wet asphalt, chirping birds, or the soft whistling of a vent.
                
                👃 2 THINGS YOU CAN SMELL:
                Sniff the ambient environment. Notice two distinct odors. The pleasant trace of morning espresso, fresh pine wood, the scent of soap, or rain outside.
                
                👅 1 THING YOU CAN TASTE:
                Acknowledge one taste inside your mouth. The lingering mint of toothpaste, the warm hint of chamomile tea, or just a sip of cold water on your tongue.
                
                Completing this simple sequence restores relative equilibrium to your amygdala, giving you the focus to take your next step calmly.
            """.trimIndent()
        ),
        LibraryResource(
            id = "sleep_sanctuary",
            category = "Sleep Better",
            title = "Creating a Sleep Sanctuary",
            description = "Practical tips to transform your bedroom into an environment optimized for restorative rest.",
            duration = "8 min read",
            type = "Read Article",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuB2cF9LrhhzboKwiGhFaZ6ijKzyHrBWbNLX-19um28AjpYqkm1Io8Z-DQ4g_aY1Fy7e8d8nKOh_vMXsr6Xuj9Hq64oGJOuGEQ3TcAuY0TqVs-Kvl5Qt4e-16l-khwHDAmxuvvYDm6Tr4FD38ovtJzy34pP8PloTk7ZpdbRNQR3h_MKMCJxkQRCdKVB2P7vKPkwobQZz23Jt8cRJRZgRxF3T0XisxBfPoJbTfTVkAxBi2myXn9Vsps-h",
            iconName = "menu_book",
            exerciseType = "article",
            content = """
                Your bedroom should serve as a sacred refuge dedicated purely to peace and recovery. When your brain links the bedroom environment exclusively with calmness and sleep, drifting off happens automatically.
                
                Here are the core pillars to rebuild your sanctuary:
                
                🌑 1. EMBRACE TOTAL DARKNESS
                Any intrusive light (LED chargers, street lamps, or screen previews) blocks the healthy generation of melatonin. Install blackouts or utilize an eye mask to signal sleepiness.
                
                ❄️ 2. DIAL DOWN THE THERMOMETRIC LEVEL
                The ideal cave temperature for healthy REM cycles sits between 60°F and 68°F (15°C - 20°C). A cooler room mimics your body's natural temperature drop prior to sleep.
                
                📵 3. DETOXIFY FROM RADIATING SCREENS
                The high-intensity blue light of cell phones tells your circadian rhythm it is mid-afternoon. Charge devices in another room, and switch to analog books/journals 45 minutes before bedtime.
                
                🕯️ 4. CHOOSE SOOTHING ESSENTIALS
                Incorporate natural relaxing smells like organic lavender sprays or chamomile essential oils. A consistent sleep ritual helps your body anticipate rest before you even lay down.
            """.trimIndent()
        ),
        LibraryResource(
            id = "box_breathing",
            category = "Anxiety Relief",
            title = "Box Breathing Practice",
            description = "Follow along with this visual breathing exercise to quickly regulate your nervous system.",
            duration = "3 min exercise",
            type = "Start Practice",
            imageUrl = "", // Fallback to icon
            iconName = "self_improvement",
            exerciseType = "breath",
            content = "Box Breathing (also known as four-square breathing) is a highly-reliable technique used by first responders to immediately calm hyper-aroused states. Follow the visual expander to regulate your breathing cycle."
        )
    )
}
