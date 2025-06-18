package service

import kotlin.random.Random
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentLinkedQueue
import config.BotConfig

class AdvertisementService {
    private val logger = LoggerFactory.getLogger(AdvertisementService::class.java)
    private val random = Random
    
    // Cooldown mechanism to avoid repeating recent advertisements
    private val recentAdvertisements = ConcurrentLinkedQueue<Int>()
    private val advertisements = listOf(
        "🧽 **SpongeBot's Absorbent Anxiety Absorber™** - Soak up your worries with our premium worry-wicking technology! *Side effects may include uncontrollable giggling.*",
        "🍔 **Krabby Patty Anxiety Pills** - The secret formula is... therapy! Now in jellyfish flavor! 💊",
        "🧠 **Squidward's Creativity Enhancer** - Turn your mundane life into modern art! Warning: May cause sudden urges to play clarinet badly.",
        "🏠 **Patrick's Rock Bottom Real Estate** - Why live under a rock when you can live under THE rock? Premium location guaranteed!",
        "🎵 **Musical Mayhem Maker 3000** - Transform any conversation into a Broadway musical! *Neighbors not included.*",
        "🍕 **Procrastination Pizza** - We'll deliver it tomorrow... or next week... or whenever we feel like it.",
        "💡 **Lightbulb Moments in a Jar** - Instant inspiration! Just add water and overthinking. *Eureka not guaranteed.*",
        "🎨 **Invisible Paint** - Perfect for painting the town clear! See-through results guaranteed or your money back!",
        "🧸 **Adult Teddy Bear Translator** - Finally understand what Mr. Snuggles has been trying to tell you all these years!",
        "🚀 **Procrastination Rocket** - Blast off to Tomorrow Land! *Departure time: eventually.*",
        "📱 **Anti-Social Media App** - Connect with yourself! Features include: talking to walls, debating with mirrors.",
        "🍜 **Instant Confidence Soup** - Just add hot water and fake it till you make it! *Confidence not actually included.*",
        "🎭 **Fake Emotion Detector** - Spot insincerity from miles away! Warning: May ruin all your relationships.",
        "🧪 **Chemistry Set for Adults** - Mix baking soda and vinegar to feel like a scientist again! *Actual science not included.*",
        "📚 **Unread Books Guilt Remover** - Finally feel better about that shelf of shame! *Reading still required separately.*",
        "🎸 **Air Guitar Amplifier** - Make your imaginary shredding audible to neighbors! Volume: unbearably loud.",
        "🍫 **Emotional Chocolate** - Tastes like your feelings! Flavors include: Monday Blues, Sunday Scaries, and Existential Dread.",
        "🎪 **Life Circus Tent** - Big enough for all your drama! *Popcorn and therapy not included.*",
        "🔮 **Fortune Cookie Wisdom Generator** - Ancient sayings like 'Your future is in another cookie' and 'Help, I'm trapped in a fortune cookie factory!'",
        "🧼 **Soap Opera Bubble Bath** - Experience dramatic plot twists in your tub! *May cause sudden urges to dramatically gasp.*",
        "🎯 **Target Practice for Life Goals** - Aim high, miss wildly! *Actual goal achievement not guaranteed.*",
        "🍰 **Cake for Breakfast Justifier** - Life's too short! Birthdays happen 365 days a year somewhere! *Diabetes not included.*",
        "🎨 **Stick Figure Art Academy** - Master the ancient art of drawing people who look like they're made of sticks!",
        "🔧 **Broken Dreams Repair Kit** - Duct tape and hope included! *Actual repair results may vary.*",
        "🎮 **Reality Escape Pod** - When life gets too real, just press pause! *Responsibility sold separately.*",
        "🍿 **Drama Detector Popcorn** - Pops when people are being dramatic! *May explode during family dinners.*",
        "🎪 **Adulting Circus** - Watch grown-ups pretend they know what they're doing! *Peanuts and existential crisis included.*",
        "🧸 **Childhood Innocence Preserver** - Keep that sense of wonder alive! *Cynicism antidote not included.*",
        "🎵 **Elevator Music Everywhere** - Turn any awkward silence into... an even more awkward silence!",
        "🍕 **Pizza Slice Life Coach** - Cheesy advice for your daily problems! *Lactose tolerance not included.*",
        "🎨 **Masterpiece Maker from Messes** - Turn your disasters into art! *Frame not included, dignity sold separately.*",
        "🔮 **Future Predictor** - Spoiler alert: Tomorrow is another day! *Accuracy not guaranteed.*",
        "🧠 **Brain Cell Backup Drive** - For when you can't remember where you put your keys... again. *Memory not included.*",
        "🎭 **Dramatic Exit Door** - Make every goodbye legendary! *Dramatic wind effects cost extra.*",
        "🍔 **Midnight Snack Justifier** - It's 3 AM somewhere! *Stomach regret not included.*",
        "🎪 **Expectations vs Reality Circus** - Front row seats to life's biggest disappointments! *Tissues included.*",
        "🧪 **Weird Science Experiment Kit** - Mix random stuff and hope for the best! *Lab safety not included.*",
        "🎸 **Karaoke Confidence Booster** - Sing like nobody's listening (they are, and they're recording)! *Dignity sold separately.*",
        "🍰 **Cake Therapy Sessions** - Because sometimes you need to talk to dessert! *Calories don't count during therapy.*",
        "🎯 **Life Direction Compass** - Points to 'confused' 99% of the time! *Actual direction not guaranteed.*",
        "🔧 **Adulting Toolkit** - Includes: fake confidence, coffee addiction, and the ability to pretend vegetables taste good.",
        "🎮 **Nostalgia Gaming Console** - Play all your childhood favorites while questioning your life choices!",
        "🍿 **Awkward Situation Popcorn** - Makes uncomfortable moments more bearable! *Social skills not included.*",
        "🎨 **Abstract Art Translator** - Finally understand what that blob on the wall means! *Understanding not guaranteed.*",
        "🔮 **Magic 8-Ball for Adults** - Answers include: 'Ask again after coffee' and 'Outlook not so good, but try wine.'",
        "🧸 **Comfort Zone Expansion Kit** - Step outside your comfort zone! *Anxiety management sold separately.*",
        "🎵 **Personal Soundtrack Generator** - Every moment gets its own theme music! *Neighbors' sanity not included.*",
        "🍕 **Procrastination Pizza Timer** - Counts down to when you should start that project you've been avoiding.",
        "🎪 **Life Skills Circus** - Watch adults struggle with basic tasks! *Popcorn and sympathy included.*",
        "🧠 **Common Sense Dispenser** - For when you need someone to state the obvious! *Wisdom not included.*",
        "🎭 **Identity Crisis Theater** - Explore all your potential selves! *Actual self-discovery not guaranteed.*",
        "🍔 **Regret Burger** - Tastes great now, terrible in the morning! *Antacids not included.*",
        "🎨 **Perfectionism Eraser** - Delete your need to be perfect! *Perfection not included, thankfully.*",
        "🔮 **Time Travel Agency** - Visit your past mistakes! *Ability to change them not included.*",
        "🧪 **Instant Motivation Powder** - Just add water and procrastination! *Actual motivation not guaranteed.*",
        "🎸 **Shower Concert Microphone** - Because your bathroom acoustics deserve professional equipment!",
        "🍰 **Guilt-Free Dessert Glasses** - See calories as friendship points! *Actual health effects may vary.*",
        "🎯 **New Year's Resolution Tracker** - Counts down to when you'll give up! *Motivation not included.*",
        "🔧 **Broken Heart Repair Shop** - We fix what love breaks! *Duct tape and ice cream included.*",
        "🎮 **Adult Responsibility Simulator** - Practice paying bills without the consequences! *Actual money not included.*",
        "🍿 **Reality TV Life** - Turn your mundane existence into must-see TV! *Drama queens not included.*",
        "🎨 **Scribble Interpreter** - Understand your artistic toddler's masterpieces! *Artistic appreciation not guaranteed.*",
        "🔮 **Fortune Teller for Introverts** - Predicts: 'You will stay home tonight' with 99% accuracy!",
        "🧸 **Adulting Training Wheels** - For when being a grown-up is too hard! *Actual maturity not included.*",
        "🎵 **Mood Ring Playlist** - Music that changes based on your emotional state! *Emotional stability not included.*",
        "🍕 **Breakfast Pizza Normalizer** - Because pizza is just flat bread with toppings! *Nutritional value not included.*",
        "🎪 **Coordination Circus** - Watch people attempt basic motor skills! *Safety net not included.*",
        "🧠 **Overthinking Prevention Service** - Stops you from analyzing every text message! *Peace of mind not guaranteed.*",
        "🎭 **Fake Confidence Mask** - Pretend you know what you're doing! *Actual competence not included.*",
        "🍔 **Impulse Purchase Justifier** - You NEEDED that thing! *Financial responsibility not included.*",
        "🎨 **Doodle Decoder Ring** - Translate your meeting notes into actual art! *Artistic talent not included.*",
        "🔮 **Future Self Motivator** - Your future self will thank you! *Gratitude not guaranteed.*",
        "🧪 **Instant Expertise Potion** - Become an expert in 5 minutes! *Actual knowledge not included.*",
        "🎸 **Imaginary Band Manager** - Book gigs for your air guitar trio! *Talent not required.*",
        "🍰 **Celebration Cake Calendar** - Every day is worth celebrating! *Reasons to celebrate not included.*",
        "🎯 **Attention Span Extender** - Focus for more than 5 minutes! *ADHD not included... wait, what were we talking about?*",
        "🔧 **Procrastination Toolkit** - Everything you need to avoid doing what you should! *Productivity not included.*",
        "🎮 **Reality Pause Button** - Stop the world, you want to get off! *Actual stopping not guaranteed.*",
        "🍿 **Awkward Silence Filler** - Never have another uncomfortable pause! *Social skills still required.*",
        "🎨 **Crayon Psychology** - Understand your emotional state through coloring choices! *Actual therapy not included.*",
        "🔮 **Magic Mirror of Truth** - Shows you exactly how you look at 3 AM! *Self-esteem not included.*",
        "🧸 **Inner Child Translator** - Understand what your younger self is trying to tell you! *Therapy not included.*",
        "🎵 **Elevator Music Mood Booster** - Makes waiting less painful! *Actual joy not guaranteed.*",
        "🍕 **Pizza Slice Philosophy** - Life lessons from your favorite food! *Wisdom not included.*",
        "🎪 **Expectations Management Circus** - Lower your standards professionally! *Disappointment not included.*",
        "🧠 **Brain Fog Dissipator** - Clear your mental haze! *Actual clarity not guaranteed.*",
        "🎭 **Personality Rental Service** - Try on different versions of yourself! *Actual self-improvement not included.*",
        "🍔 **Midnight Regret Burger** - Tastes great until morning! *Shame not included.*",
        "🎨 **Mess Appreciation Society** - Find beauty in chaos! *Actual organization not included.*",
        "🔮 **Decision Maker 3000** - Flip a coin digitally! *Good decisions not guaranteed.*",
        "🧪 **Instant Wisdom Tablets** - Swallow your pride with these! *Actual intelligence not included.*",
        "🎸 **Karaoke Confidence Pills** - Sing like you're alone in the shower! *Talent not included.*",
        "🍰 **Stress Eating Justifier** - It's not emotional eating, it's fuel! *Emotional stability not included.*",
        "🎯 **Life Goal Randomizer** - Because sometimes you need direction! *Actual achievement not guaranteed.*",
        "🔧 **Adulting Emergency Kit** - For when you realize you're supposed to be a grown-up! *Maturity not included.*",
        "🎮 **Nostalgia VR Headset** - Relive the good old days when you thought you'd have it figured out by now!",
        "🍿 **Drama Appreciation Popcorn** - Makes other people's problems more entertaining! *Empathy not included.*",
        "🎨 **Stick Figure Masterclass** - Advanced techniques for drawing people who look like twigs! *Artistic talent not required.*",
        "🔮 **Future Anxiety Predictor** - Worry about things that haven't happened yet! *Peace of mind not included.*",
        "🧸 **Comfort Zone Expansion Pack** - Includes: mild discomfort, questioning everything, and growth! *Comfort not guaranteed.*",
        "🎵 **Personal Theme Song Generator** - Everyone needs an entrance theme! *Musical talent not included.*",
        "🍕 **Procrastination Pizza Timer** - Counts down to when you should start that thing you're avoiding! *Motivation not included.*",
        "🎪 **Life Coordination Circus** - Watch adults attempt to have their lives together! *Success not guaranteed.*",
        "🧠 **Memory Palace Rental** - Spacious mind palace available! *Actual memories not included.*",
        "🎭 **Fake It Till You Make It Academy** - Master the art of pretending! *Actual competence comes later.*",
        "🍔 **Impulse Decision Burger** - Tastes like regret but with cheese! *Good judgment not included.*",        "🎨 **Abstract Life Interpreter** - Make sense of your chaotic existence! *Actual understanding not guaranteed.*"
    )
      /**
     * Get a random advertisement message, avoiding recently shown ones
     */
    fun getRandomAdvertisement(): String {
        val maxRecentAds = BotConfig.getAdvertisementCooldownSize()
        val availableIndices = advertisements.indices.filter { index -> 
            !recentAdvertisements.contains(index)
        }
        
        val selectedIndex = if (availableIndices.isNotEmpty()) {
            // Select from non-recent advertisements
            availableIndices.random(random)
        } else {
            // If all advertisements have been shown recently, clear the history and pick any
            logger.debug("All advertisements recently shown, clearing history")
            recentAdvertisements.clear()
            advertisements.indices.random(random)
        }
        
        // Add to recent advertisements
        recentAdvertisements.offer(selectedIndex)
        
        // Keep only the most recent advertisements in memory
        while (recentAdvertisements.size > maxRecentAds) {
            recentAdvertisements.poll()
        }
        
        val ad = advertisements[selectedIndex]
        logger.debug("Selected advertisement #$selectedIndex: ${ad.take(50)}... (${recentAdvertisements.size}/$maxRecentAds recent ads in memory)")
        return ad
    }
    
    /**
     * Determine if an advertisement should be shown based on configured probability
     * @param probability The probability (0.0 to 1.0) of showing an advertisement
     */
    fun shouldShowAdvertisement(probability: Double = 0.075): Boolean { // 7.5% default chance
        val shouldShow = random.nextDouble() < probability
        logger.debug("Advertisement probability check: $probability, result: $shouldShow")
        return shouldShow
    }
      /**
     * Get the total number of available advertisements
     */
    fun getAdvertisementCount(): Int = advertisements.size
    
    /**
     * Clear the recent advertisements history to reset cooldown
     */
    fun clearCooldownHistory() {
        recentAdvertisements.clear()
        logger.info("Advertisement cooldown history cleared")
    }
    
    /**
     * Get the current cooldown status
     */
    fun getCooldownStatus(): String {
        val maxRecentAds = BotConfig.getAdvertisementCooldownSize()
        return "Recent advertisements: ${recentAdvertisements.size}/$maxRecentAds"
    }
}
