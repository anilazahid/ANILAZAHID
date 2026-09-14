package com.anilazahid.quiz;

import java.util.ArrayList;
import java.util.List;

public final class QuizBank {
    public static final int DAILY_REWARD_COINS = 20;

    public static class StarterCategory {
        public final String title;
        public final String icon;
        public final String[][] questions;

        StarterCategory(String title, String icon, String[][] questions) {
            this.title = title;
            this.icon = icon;
            this.questions = questions;
        }
    }

    private QuizBank() {}

    public static List<StarterCategory> categories() {
        List<StarterCategory> categories = new ArrayList<>();
        categories.add(new StarterCategory("General Knowledge", "🧩", new String[][] {
            {"What is the capital of France?", "Paris", "Rome", "Madrid", "Berlin", "0"},
            {"How many continents are there?", "Seven", "Five", "Six", "Eight", "0"},
            {"Which is the largest ocean?", "Pacific Ocean", "Atlantic Ocean", "Indian Ocean", "Arctic Ocean", "0"},
            {"What is the currency of Japan?", "Yen", "Won", "Baht", "Rupee", "0"},
            {"Which animal is known as the king of the jungle?", "Lion", "Tiger", "Elephant", "Leopard", "0"},
            {"How many days are in a leap year?", "366", "365", "364", "360", "0"},
            {"Which instrument measures temperature?", "Thermometer", "Barometer", "Compass", "Hygrometer", "0"},
            {"What is the tallest land animal?", "Giraffe", "Elephant", "Horse", "Camel", "0"},
            {"Which color is made by mixing blue and yellow?", "Green", "Orange", "Purple", "Brown", "0"},
            {"How many sides does a hexagon have?", "Six", "Five", "Seven", "Eight", "0"},
            {"Which shape has four equal sides?", "Square", "Triangle", "Circle", "Oval", "0"},
            {"What is the largest planet in our solar system?", "Jupiter", "Earth", "Mars", "Venus", "0"},
            {"How many hours are in one day?", "24", "12", "48", "60", "0"},
            {"Which bird is often a symbol of peace?", "Dove", "Eagle", "Crow", "Sparrow", "0"},
            {"What is frozen water called?", "Ice", "Steam", "Mist", "Frosting", "0"},
            {"Which metal is liquid at room temperature?", "Mercury", "Iron", "Copper", "Gold", "0"},
            {"How many letters are in the English alphabet?", "26", "24", "25", "28", "0"},
            {"Which season follows spring?", "Summer", "Winter", "Autumn", "Monsoon", "0"},
            {"What is the opposite of north?", "South", "East", "West", "Up", "0"},
            {"Which object shows a map direction?", "Compass", "Clock", "Thermometer", "Ruler", "0"}
        }));
        categories.add(new StarterCategory("Science", "🧪", new String[][] {
            {"What gas do plants absorb?", "Carbon dioxide", "Oxygen", "Nitrogen", "Hydrogen", "0"},
            {"What is H2O commonly called?", "Water", "Salt", "Oxygen", "Hydrogen", "0"},
            {"Which planet is closest to the Sun?", "Mercury", "Venus", "Earth", "Mars", "0"},
            {"What force pulls objects toward Earth?", "Gravity", "Friction", "Magnetism", "Pressure", "0"},
            {"What is the center of an atom called?", "Nucleus", "Cell", "Core", "Electron", "0"},
            {"Which organ pumps blood?", "Heart", "Lung", "Liver", "Kidney", "0"},
            {"What process do plants use to make food?", "Photosynthesis", "Digestion", "Respiration", "Evaporation", "0"},
            {"How many bones are in an adult human body approximately?", "206", "106", "306", "256", "0"},
            {"Which vitamin is produced by sunlight on skin?", "Vitamin D", "Vitamin A", "Vitamin C", "Vitamin K", "0"},
            {"What is the boiling point of water at sea level?", "100 C", "50 C", "75 C", "150 C", "0"},
            {"What is the basic unit of life?", "Cell", "Atom", "Organ", "Tissue", "0"},
            {"Which gas do humans need to breathe?", "Oxygen", "Carbon dioxide", "Helium", "Hydrogen", "0"},
            {"What is the nearest star to Earth?", "The Sun", "Sirius", "The Moon", "Mars", "0"},
            {"Which state of matter has a fixed shape?", "Solid", "Liquid", "Gas", "Plasma only", "0"},
            {"What instrument magnifies tiny objects?", "Microscope", "Telescope", "Periscope", "Compass", "0"},
            {"Which blood cells help fight infection?", "White blood cells", "Red blood cells", "Platelets", "Plasma", "0"},
            {"What is the study of weather called?", "Meteorology", "Geology", "Biology", "Astronomy", "0"},
            {"Which natural satellite orbits Earth?", "The Moon", "The Sun", "Mars", "Venus", "0"},
            {"What energy comes from moving air?", "Wind energy", "Solar energy", "Chemical energy", "Nuclear energy", "0"},
            {"Which part of a plant absorbs water?", "Roots", "Flower", "Fruit", "Stem tip", "0"}
        }));
        categories.add(new StarterCategory("Mathematics", "➗", new String[][] {
            {"What is 12 + 8?", "20", "18", "22", "24", "0"},
            {"What is 9 x 7?", "63", "56", "72", "49", "0"},
            {"What is half of 100?", "50", "25", "10", "75", "0"},
            {"What is 144 divided by 12?", "12", "10", "14", "16", "0"},
            {"How many degrees are in a right angle?", "90", "45", "180", "360", "0"},
            {"What is the square root of 81?", "9", "8", "7", "6", "0"},
            {"What is 15 percent of 100?", "15", "10", "20", "5", "0"},
            {"What is the next prime number after 7?", "11", "9", "10", "13", "0"},
            {"What is 3 squared?", "9", "6", "12", "3", "0"},
            {"How many sides does a triangle have?", "3", "4", "5", "6", "0"},
            {"What is 100 minus 37?", "63", "67", "73", "57", "0"},
            {"What is 8 times 8?", "64", "56", "72", "48", "0"},
            {"What is one quarter of 20?", "5", "4", "10", "15", "0"},
            {"How many degrees are in a straight angle?", "180", "90", "270", "360", "0"},
            {"What is 2 to the power of 5?", "32", "10", "25", "64", "0"},
            {"What is the perimeter of a square with side 3?", "12", "9", "6", "15", "0"},
            {"Which number is even?", "18", "15", "21", "27", "0"},
            {"What is 45 divided by 5?", "9", "8", "10", "5", "0"},
            {"What is the value of 7 + 6 x 2?", "19", "26", "20", "14", "0"},
            {"How many meters are in one kilometer?", "1000", "100", "10", "10000", "0"}
        }));
        categories.add(new StarterCategory("English", "📝", new String[][] {
            {"Which word is a noun?", "Book", "Quickly", "Run", "Beautiful", "0"},
            {"What is the opposite of ancient?", "Modern", "Old", "Historic", "Past", "0"},
            {"Choose the plural of child.", "Children", "Childs", "Childes", "Childrens", "0"},
            {"Which word means happy?", "Joyful", "Angry", "Tired", "Silent", "0"},
            {"What is the past tense of go?", "Went", "Gone", "Goed", "Going", "0"},
            {"Which punctuation ends a question?", "Question mark", "Comma", "Full stop", "Colon", "0"},
            {"Choose the correctly spelled word.", "Necessary", "Neccessary", "Necesary", "Necassary", "0"},
            {"What is an adjective?", "A describing word", "An action word", "A naming word", "A joining word", "0"},
            {"Which is a synonym for begin?", "Start", "Stop", "Finish", "Close", "0"},
            {"What is the comparative form of good?", "Better", "Best", "Gooder", "More good", "0"},
            {"Which word is a verb?", "Jump", "Blue", "Chair", "Quietly", "0"},
            {"What is the opposite of difficult?", "Easy", "Hard", "Heavy", "Late", "0"},
            {"Choose the correct article: ___ apple.", "An", "A", "Thee", "No", "0"},
            {"Which word is spelled correctly?", "Separate", "Seperate", "Seperete", "Separete", "0"},
            {"What is the past tense of eat?", "Ate", "Eated", "Eating", "Eat", "0"},
            {"Which word is an adverb?", "Quickly", "Quick", "Quicker", "Quickness", "0"},
            {"What is the plural of mouse?", "Mice", "Mouses", "Mousees", "Mices", "0"},
            {"Which sentence ends with an exclamation mark?", "What a beautiful day!", "Where are you?", "I read a book.", "Please sit down", "0"},
            {"What is a group of words with a complete meaning?", "Sentence", "Letter", "Syllable", "Sound", "0"},
            {"Which word rhymes with light?", "Night", "Late", "Lot", "Let", "0"}
        }));
        categories.add(new StarterCategory("Computer & Technology", "💻", new String[][] {
            {"What does CPU stand for?", "Central Processing Unit", "Computer Power Unit", "Central Program Utility", "Control Processing User", "0"},
            {"Which device displays computer output?", "Monitor", "Keyboard", "Mouse", "Scanner", "0"},
            {"What does Wi-Fi provide?", "Wireless network access", "Electric power", "Printer ink", "File storage only", "0"},
            {"Which is an operating system?", "Android", "Google", "Intel", "Wi-Fi", "0"},
            {"What is a web address called?", "URL", "CPU", "RAM", "USB", "0"},
            {"Which key removes text before the cursor?", "Backspace", "Shift", "Tab", "Escape", "0"},
            {"What does USB commonly connect?", "Devices and accessories", "Only televisions", "Water pipes", "Paper files", "0"},
            {"Which one is a search engine?", "Google", "Android", "Windows", "Bluetooth", "0"},
            {"What is cloud storage used for?", "Storing files online", "Cooling a computer", "Printing photos", "Charging batteries", "0"},
            {"What does RAM help a computer do?", "Temporarily store working data", "Print documents", "Connect to satellites", "Play sound only", "0"},
            {"What does HTML help create?", "Web pages", "Electric motors", "Music records", "Paper books", "0"},
            {"Which device moves the pointer on a computer?", "Mouse", "Monitor", "Printer", "Speaker", "0"},
            {"What is a strong password designed to protect?", "An account", "A keyboard key", "A monitor screen", "A power cable", "0"},
            {"Which file type is commonly an image?", "JPG", "MP3", "TXT", "EXE only", "0"},
            {"What is Bluetooth mainly used for?", "Short-range wireless connections", "Long-distance flights", "Cooking food", "Printing newspapers", "0"},
            {"Which storage device uses flash memory?", "USB drive", "Paper folder", "Desk lamp", "Microphone", "0"},
            {"What does a browser open?", "Websites", "Fuel tanks", "Bank vaults", "Cameras only", "0"},
            {"What is a computer virus?", "Malicious software", "A hardware cable", "A safe document", "A screen setting", "0"},
            {"Which symbol is common in an email address?", "@", "# only", "%", "&", "0"},
            {"What does GPS help determine?", "Location", "Temperature", "Blood type", "Screen size", "0"}
        }));
        categories.add(new StarterCategory("History", "📜", new String[][] {
            {"Who was the first president of the United States?", "George Washington", "Abraham Lincoln", "Thomas Jefferson", "John Adams", "0"},
            {"The ancient pyramids are strongly associated with which country?", "Egypt", "Greece", "China", "Mexico", "0"},
            {"In which year did World War II end?", "1945", "1939", "1918", "1950", "0"},
            {"Who wrote the Declaration of Independence draft?", "Thomas Jefferson", "Benjamin Franklin", "George Washington", "John Hancock", "0"},
            {"Which civilization built Machu Picchu?", "Inca", "Roman", "Viking", "Maya", "0"},
            {"The Renaissance began mainly in which country?", "Italy", "France", "England", "Spain", "0"},
            {"Who was known as the Maid of Orleans?", "Joan of Arc", "Cleopatra", "Queen Victoria", "Marie Curie", "0"},
            {"Which wall fell in 1989?", "Berlin Wall", "Great Wall", "Hadrian's Wall", "Western Wall", "0"},
            {"Who was the first person to walk on the Moon?", "Neil Armstrong", "Yuri Gagarin", "Buzz Aldrin", "Alan Shepard", "0"},
            {"The Roman Empire was centered around which city?", "Rome", "Athens", "Paris", "Cairo", "0"},
            {"Who was the first emperor of Rome?", "Augustus", "Nero", "Caesar", "Trajan", "0"},
            {"Which ancient people used hieroglyphs?", "Egyptians", "Vikings", "Aztecs", "Mongols", "0"},
            {"The Magna Carta was signed in which country?", "England", "France", "Italy", "Spain", "0"},
            {"Who painted the Mona Lisa?", "Leonardo da Vinci", "Michelangelo", "Raphael", "Van Gogh", "0"},
            {"Which ship sank in 1912?", "Titanic", "Mayflower", "Endeavour", "Beagle", "0"},
            {"Who was called the Iron Lady?", "Margaret Thatcher", "Marie Curie", "Florence Nightingale", "Rosa Parks", "0"},
            {"Which civilization developed democracy in Athens?", "Ancient Greeks", "Romans", "Persians", "Egyptians", "0"},
            {"The Cold War was mainly between the United States and what?", "Soviet Union", "Canada", "Japan", "Brazil", "0"},
            {"Who discovered penicillin?", "Alexander Fleming", "Isaac Newton", "Louis Pasteur", "Charles Darwin", "0"},
            {"Which explorer reached the Americas in 1492?", "Christopher Columbus", "Marco Polo", "James Cook", "Ferdinand Magellan", "0"}
        }));
        categories.add(new StarterCategory("Geography", "🌍", new String[][] {
            {"Which is the largest country by area?", "Russia", "Canada", "China", "United States", "0"},
            {"What is the longest river in Africa?", "Nile", "Congo", "Niger", "Zambezi", "0"},
            {"Mount Everest is in which mountain range?", "Himalayas", "Alps", "Andes", "Rockies", "0"},
            {"Which desert is in northern Africa?", "Sahara", "Gobi", "Kalahari", "Mojave", "0"},
            {"What is the capital of Australia?", "Canberra", "Sydney", "Melbourne", "Perth", "0"},
            {"Which continent is Brazil in?", "South America", "North America", "Europe", "Africa", "0"},
            {"Which country has the city of Istanbul?", "Turkey", "Greece", "Iran", "Egypt", "0"},
            {"The Equator divides Earth into which two halves?", "Northern and Southern", "Eastern and Western", "Summer and Winter", "Land and Sea", "0"},
            {"Which sea lies between Europe and Africa?", "Mediterranean Sea", "Red Sea", "Arabian Sea", "Baltic Sea", "0"},
            {"What is the capital of Canada?", "Ottawa", "Toronto", "Vancouver", "Montreal", "0"},
            {"Which is the smallest continent?", "Australia", "Europe", "Antarctica", "South America", "0"},
            {"What is the capital of Italy?", "Rome", "Milan", "Venice", "Naples", "0"},
            {"Which ocean is east of Africa?", "Indian Ocean", "Atlantic Ocean", "Pacific Ocean", "Arctic Ocean", "0"},
            {"What is the capital of Egypt?", "Cairo", "Alexandria", "Giza", "Luxor", "0"},
            {"Which line has a latitude of zero degrees?", "Equator", "Prime Meridian", "Tropic of Cancer", "Arctic Circle", "0"},
            {"Which country is home to the Taj Mahal?", "India", "Nepal", "Pakistan", "Bangladesh", "0"},
            {"What is the capital of Japan?", "Tokyo", "Kyoto", "Osaka", "Hiroshima", "0"},
            {"Which mountain range includes Mount Everest?", "Himalayas", "Andes", "Alps", "Urals", "0"},
            {"Which country has the longest coastline?", "Canada", "Russia", "Australia", "China", "0"},
            {"What is the capital of Kenya?", "Nairobi", "Mombasa", "Kampala", "Addis Ababa", "0"}
        }));
        categories.add(new StarterCategory("Pakistan Studies", "🇵🇰", new String[][] {
            {"What is the capital of Pakistan?", "Islamabad", "Karachi", "Lahore", "Peshawar", "0"},
            {"Who is the founder of Pakistan?", "Muhammad Ali Jinnah", "Allama Iqbal", "Liaquat Ali Khan", "Sir Syed Ahmad Khan", "0"},
            {"Pakistan gained independence in which year?", "1947", "1940", "1956", "1965", "0"},
            {"What is Pakistan's national language?", "Urdu", "Punjabi", "English", "Sindhi", "0"},
            {"Which is Pakistan's national flower?", "Jasmine", "Rose", "Tulip", "Lily", "0"},
            {"Which mountain is the second-highest in the world?", "K2", "Nanga Parbat", "Rakaposhi", "Broad Peak", "0"},
            {"What is the national animal of Pakistan?", "Markhor", "Lion", "Snow leopard", "Himalayan bear", "0"},
            {"Which city is called the City of Gardens?", "Lahore", "Quetta", "Multan", "Gwadar", "0"},
            {"The Indus River flows mainly through which country?", "Pakistan", "Nepal", "Bangladesh", "Sri Lanka", "0"},
            {"What is Pakistan's national sport traditionally recognized as?", "Field hockey", "Cricket", "Football", "Squash", "0"},
            {"What is the national bird of Pakistan?", "Chukar", "Eagle", "Peacock", "Dove", "0"},
            {"Which city is Pakistan's largest by population?", "Karachi", "Lahore", "Islamabad", "Quetta", "0"},
            {"What is the national flower of Pakistan?", "Jasmine", "Rose", "Sunflower", "Lily", "0"},
            {"Which pass connects Pakistan and Afghanistan?", "Khyber Pass", "Bolan Pass", "Karakoram Pass", "Lowari Pass", "0"},
            {"Who wrote Pakistan's national anthem lyrics?", "Hafeez Jalandhari", "Faiz Ahmed Faiz", "Allama Iqbal", "Josh Malihabadi", "0"},
            {"Which sea borders Pakistan's south?", "Arabian Sea", "Red Sea", "Caspian Sea", "Mediterranean Sea", "0"},
            {"What is the highest peak in Pakistan?", "K2", "Nanga Parbat", "Tirich Mir", "Rakaposhi", "0"},
            {"Which city is known as the City of Lights in Pakistan?", "Karachi", "Lahore", "Multan", "Peshawar", "0"},
            {"Pakistan's first constitution was adopted in which year?", "1956", "1947", "1962", "1973", "0"},
            {"Which language is widely spoken in Sindh?", "Sindhi", "Balochi", "Pashto", "Punjabi", "0"}
        }));
        categories.add(new StarterCategory("Islamic Knowledge", "🕌", new String[][] {
            {"How many pillars are there in Islam?", "Five", "Four", "Six", "Seven", "0"},
            {"What is the holy book of Islam?", "The Quran", "The Bible", "The Torah", "The Vedas", "0"},
            {"How many obligatory prayers are there each day?", "Five", "Three", "Four", "Seven", "0"},
            {"In which month do Muslims fast?", "Ramadan", "Shawwal", "Muharram", "Rajab", "0"},
            {"What is the direction of prayer called?", "Qiblah", "Mihrab", "Minbar", "Hijrah", "0"},
            {"Which city is home to the Kaaba?", "Makkah", "Madinah", "Jerusalem", "Cairo", "0"},
            {"What is the charity obligatory for eligible Muslims called?", "Zakat", "Sawm", "Hajj", "Shahadah", "0"},
            {"Which Islamic month begins the lunar year?", "Muharram", "Ramadan", "Dhul Hijjah", "Safar", "0"},
            {"What is the pilgrimage to Makkah called?", "Hajj", "Umrah", "Zakat", "Salah", "0"},
            {"What is the declaration of faith called?", "Shahadah", "Sawm", "Zakat", "Tawaf", "0"},
            {"What is the first month of the Islamic calendar?", "Muharram", "Rajab", "Ramadan", "Shawwal", "0"},
            {"Which prophet is associated with the ark?", "Nuh", "Musa", "Yusuf", "Yunus", "0"},
            {"What is the Friday congregational prayer called?", "Jumu'ah", "Tarawih", "Tahajjud", "Eid", "0"},
            {"Which city is the Prophet's Mosque located in?", "Madinah", "Makkah", "Jerusalem", "Cairo", "0"},
            {"What is fasting called in Arabic?", "Sawm", "Salah", "Hajj", "Zakat", "0"},
            {"Which angel brought revelation to prophets?", "Jibril", "Mikail", "Israfil", "Malik", "0"},
            {"What is the festival after Ramadan called?", "Eid al-Fitr", "Eid al-Adha", "Ashura", "Mawlid", "0"},
            {"In which direction do Muslims face during prayer?", "Toward the Kaaba", "Toward the Sun", "Toward Madinah", "Toward the sea", "0"},
            {"What is the night of power called?", "Laylat al-Qadr", "Laylat al-Miraj", "Ashura", "Arafah", "0"},
            {"Which pilgrimage festival honors Prophet Ibrahim's obedience?", "Eid al-Adha", "Eid al-Fitr", "Ashura", "Jumu'ah", "0"}
        }));
        categories.add(new StarterCategory("Sports", "⚽", new String[][] {
            {"How many players are on a football team on the field?", "11", "9", "10", "12", "0"},
            {"Which sport uses a bat, ball and wickets?", "Cricket", "Tennis", "Hockey", "Baseball", "0"},
            {"How many rings are on the Olympic flag?", "Five", "Four", "Six", "Seven", "0"},
            {"In tennis, what score follows 30?", "40", "45", "50", "60", "0"},
            {"Which sport is played at Wimbledon?", "Tennis", "Golf", "Rugby", "Cricket", "0"},
            {"What color card sends a football player off?", "Red", "Yellow", "Blue", "Green", "0"},
            {"How long is an Olympic swimming pool?", "50 meters", "25 meters", "75 meters", "100 meters", "0"},
            {"Which sport has a slam dunk?", "Basketball", "Volleyball", "Handball", "Netball", "0"},
            {"What is the highest score with one dart?", "60", "50", "100", "30", "0"},
            {"Which country hosted the first modern Olympics?", "Greece", "France", "United Kingdom", "Italy", "0"},
            {"How many points is a touchdown worth in American football?", "6", "3", "7", "1", "0"},
            {"Which sport uses a shuttlecock?", "Badminton", "Tennis", "Squash", "Table tennis", "0"},
            {"How many players are on a basketball team on court?", "5", "6", "7", "11", "0"},
            {"Which country is famous for sumo wrestling?", "Japan", "China", "Korea", "Thailand", "0"},
            {"What is a score of zero in tennis called?", "Love", "Nil", "Duck", "Blank", "0"},
            {"Which sport awards the green jacket at the Masters?", "Golf", "Cricket", "Rugby", "Cycling", "0"},
            {"How many bases are on a baseball field?", "Four", "Three", "Five", "Six", "0"},
            {"Which athlete competes in a marathon?", "Runner", "Swimmer", "Goalkeeper", "Jockey only", "0"},
            {"What is the standard distance of a marathon?", "42.195 km", "10 km", "21 km", "50 km", "0"},
            {"Which sport has a goalkeeper and a goal net?", "Hockey", "Golf", "Boxing", "Archery", "0"}
        }));
        categories.add(new StarterCategory("World Knowledge", "🌐", new String[][] {
            {"What is the most widely spoken native language?", "Mandarin Chinese", "English", "Spanish", "Arabic", "0"},
            {"Which organization uses the acronym UN?", "United Nations", "Universal Network", "Union of Nations", "United Navy", "0"},
            {"What is the currency of the United Kingdom?", "Pound sterling", "Euro", "Dollar", "Franc", "0"},
            {"Which country is famous for the Eiffel Tower?", "France", "Italy", "Germany", "Belgium", "0"},
            {"What is the largest mammal?", "Blue whale", "Elephant", "Giraffe", "Orca", "0"},
            {"Which festival is known as the festival of lights in India?", "Diwali", "Holi", "Eid", "Vaisakhi", "0"},
            {"Which country is shaped like a boot?", "Italy", "Portugal", "Chile", "Greece", "0"},
            {"What is the main language of Brazil?", "Portuguese", "Spanish", "English", "French", "0"},
            {"Which city is famous for the Statue of Liberty?", "New York", "London", "Paris", "Sydney", "0"},
            {"Which planet is known as the Red Planet?", "Mars", "Jupiter", "Venus", "Saturn", "0"},
            {"Which country is famous for the Great Wall?", "China", "Japan", "India", "Mongolia", "0"},
            {"What is the currency of the United States?", "Dollar", "Pound", "Euro", "Yen", "0"},
            {"Which global event brings countries together every four years in sport?", "Olympic Games", "World Expo", "United Nations", "World Cup only", "0"},
            {"What is the largest hot desert?", "Sahara", "Gobi", "Arabian", "Kalahari", "0"},
            {"Which country is known for the pyramids at Giza?", "Egypt", "Sudan", "Mexico", "Greece", "0"},
            {"What is the official language of Mexico?", "Spanish", "Portuguese", "English", "French", "0"},
            {"Which country is home to the city of Mecca?", "Saudi Arabia", "Jordan", "Egypt", "Turkey", "0"},
            {"Which ocean is the largest?", "Pacific Ocean", "Atlantic Ocean", "Indian Ocean", "Southern Ocean", "0"},
            {"What is the currency of China?", "Yuan", "Yen", "Won", "Rupee", "0"},
            {"Which language is mainly spoken in Spain?", "Spanish", "Italian", "German", "Arabic", "0"}
        }));
        categories.add(new StarterCategory("Everyday Life", "🏠", new String[][] {
            {"Which appliance keeps food cold?", "Refrigerator", "Oven", "Kettle", "Toaster", "0"},
            {"What should you do before eating?", "Wash your hands", "Run outside", "Skip water", "Sleep", "0"},
            {"Which item is used to tell time?", "Clock", "Ruler", "Scale", "Compass", "0"},
            {"What do we use an umbrella for?", "Protection from rain", "Cooking food", "Cutting paper", "Measuring distance", "0"},
            {"Which meal is usually eaten in the morning?", "Breakfast", "Lunch", "Dinner", "Supper", "0"},
            {"What does a red traffic light mean?", "Stop", "Go faster", "Turn around", "Park anywhere", "0"},
            {"Which material is commonly used to write on a chalkboard?", "Chalk", "Wax", "Soap", "Glue", "0"},
            {"What is used to unlock a traditional door?", "Key", "Spoon", "Brush", "Rope", "0"},
            {"Which bin usually takes paper for recycling?", "Recycling bin", "Food freezer", "Medicine cabinet", "Sink", "0"},
            {"What should you do to cross a road safely?", "Look both ways", "Close your eyes", "Run without looking", "Stand in the road", "0"},
            {"Which tool is used to tighten a screw?", "Screwdriver", "Hammer", "Ruler", "Brush", "0"},
            {"What do we use to dry after a bath?", "Towel", "Plate", "Pillow", "Notebook", "0"},
            {"Which room is normally used for cooking?", "Kitchen", "Bedroom", "Garage", "Study", "0"},
            {"What should you do with rubbish?", "Put it in a bin", "Leave it on the floor", "Hide it in food", "Throw it in a river", "0"},
            {"Which item protects your head on a bicycle?", "Helmet", "Scarf", "Glove", "Belt", "0"},
            {"What do plants need to grow?", "Water and light", "Plastic and smoke", "Salt and oil", "Metal and sand only", "0"},
            {"Which appliance washes clothes?", "Washing machine", "Microwave", "Fan", "Freezer", "0"},
            {"What is used to measure body temperature?", "Thermometer", "Ruler", "Scale", "Clock", "0"},
            {"Which item is normally worn on your feet?", "Shoes", "Gloves", "Hat", "Tie", "0"},
            {"What should you do before crossing at a zebra crossing?", "Check for traffic", "Close your eyes", "Sit down", "Run into traffic", "0"}
        }));
        categories.add(new StarterCategory("Current Affairs", "📰", new String[][] {
            {"What does a weather forecast describe?", "Expected weather", "Past rulers", "Computer speed", "Food prices only", "0"},
            {"Which organization coordinates international public health?", "World Health Organization", "World Bank", "FIFA", "UNICEF only", "0"},
            {"What is an election used to choose?", "Representatives", "Weather", "Sports equipment", "School subjects", "0"},
            {"What does a census count?", "People and households", "Only cars", "Rain clouds", "Books only", "0"},
            {"Which global issue involves rising average temperatures?", "Climate change", "Road safety", "Literacy", "Currency exchange", "0"},
            {"What is a renewable energy source?", "Solar power", "Coal", "Petrol", "Natural gas", "0"},
            {"What does a peace agreement aim to end?", "Conflict", "Education", "Traffic", "Rainfall", "0"},
            {"Which institution makes laws in a country?", "Parliament", "Hospital", "Airport", "Museum", "0"},
            {"What does GDP broadly measure?", "Economic output", "Population height", "Rainfall only", "Sports scores", "0"},
            {"What is a public health campaign designed to improve?", "Community health", "Road length", "Movie ratings", "Mountain height", "0"},
            {"What does recycling help reduce?", "Waste", "Sunlight", "Oxygen", "Learning", "0"},
            {"Which body often sets national monetary policy?", "Central bank", "Fire service", "Library", "Sports club", "0"},
            {"What is a referendum?", "A public vote on an issue", "A weather report", "A court building", "A school exam", "0"},
            {"What does a humanitarian organization provide?", "Help to people in need", "Movie tickets", "Car repairs", "Sports scores", "0"},
            {"Which issue concerns safe access to clean water?", "Water security", "Space travel", "Fashion", "Music theory", "0"},
            {"What is inflation?", "A general rise in prices", "A fall in daylight", "A sports rule", "A type of cloud", "0"},
            {"What does a vaccination help the body develop?", "Protection from disease", "Stronger bones instantly", "Better eyesight", "Taller height", "0"},
            {"What is diplomacy mainly used for?", "Managing relations between countries", "Repairing roads", "Growing crops", "Measuring oceans", "0"},
            {"What is a news headline?", "A brief title for a report", "A legal passport", "A weather instrument", "A computer cable", "0"},
            {"What does emergency relief provide?", "Immediate help after a crisis", "Long-term entertainment", "School grades", "Travel visas", "0"}
        }));
        categories.add(new StarterCategory("Logical Reasoning", "🧠", new String[][] {
            {"What comes next: 2, 4, 6, 8?", "10", "9", "11", "12", "0"},
            {"If all cats are animals, is every cat an animal?", "Yes", "No", "Only at night", "Cannot be known", "0"},
            {"Which item does not belong: apple, banana, carrot, mango?", "Carrot", "Apple", "Banana", "Mango", "0"},
            {"What comes next: A, C, E, G?", "I", "H", "J", "K", "0"},
            {"If today is Monday, what day is after tomorrow?", "Wednesday", "Tuesday", "Thursday", "Sunday", "0"},
            {"Which number is missing: 5, 10, 15, __, 25?", "20", "18", "22", "30", "0"},
            {"A shape with three sides is a?", "Triangle", "Square", "Circle", "Pentagon", "0"},
            {"Which word is different: run, walk, blue, jump?", "Blue", "Run", "Walk", "Jump", "0"},
            {"If one pen costs 3 coins, how many cost 9 coins?", "3", "2", "4", "6", "0"},
            {"What is the opposite direction of left?", "Right", "Up", "Down", "Back", "0"},
            {"What comes next: 1, 3, 5, 7?", "9", "8", "10", "11", "0"},
            {"Which is heavier: one kilogram of iron or cotton?", "They weigh the same", "Iron", "Cotton", "Cannot compare", "0"},
            {"If B is taller than C and C taller than D, who is shortest?", "D", "B", "C", "They are equal", "0"},
            {"Which number is odd?", "17", "20", "24", "30", "0"},
            {"What comes next: 10, 20, 30, 40?", "50", "45", "55", "60", "0"},
            {"If a clock shows 3, what angle is roughly formed by its hands at 3 o'clock?", "90 degrees", "45 degrees", "180 degrees", "360 degrees", "0"},
            {"Which pair has the same relationship as hot and cold?", "Big and small", "Fast and run", "Blue and sky", "Cup and drink", "0"},
            {"If you have 4 pairs of socks, how many socks are there?", "8", "4", "6", "12", "0"},
            {"Which number completes 3 + __ = 10?", "7", "6", "8", "13", "0"},
            {"What comes next: 100, 90, 80, 70?", "60", "65", "50", "75", "0"}
        }));
        categories.add(new StarterCategory("Entertainment", "🎬", new String[][] {
            {"Which device is used to watch films at home?", "Television", "Toaster", "Compass", "Ruler", "0"},
            {"What do actors perform in?", "Films and plays", "Weather reports only", "Road races", "Cooking pans", "0"},
            {"Which instrument has black and white keys?", "Piano", "Flute", "Drum", "Guitar", "0"},
            {"What is a book of drawings that tells a story called?", "Comic book", "Dictionary", "Atlas", "Recipe", "0"},
            {"Which art form uses a camera to capture images?", "Photography", "Sculpture", "Dance", "Poetry", "0"},
            {"What is a song performed by one person called?", "Solo", "Duo", "Trio", "Chorus", "0"},
            {"Which genre often makes people laugh?", "Comedy", "Horror", "Documentary", "Tragedy", "0"},
            {"What is the person who directs a film called?", "Director", "Referee", "Pilot", "Editor only", "0"},
            {"Which instrument is commonly played with a bow?", "Violin", "Trumpet", "Drum", "Piano", "0"},
            {"What do people watch at a cinema?", "Films", "Rain gauges", "Maps", "Textbooks", "0"},
            {"What is a live performance by musicians called?", "Concert", "Census", "Lecture only", "Parade route", "0"},
            {"Which genre tells a frightening story?", "Horror", "Comedy", "Romance", "Documentary", "0"},
            {"What is a collection of songs released together called?", "Album", "Chapter", "Canvas", "Ticket", "0"},
            {"Which activity uses a stage and choreography?", "Dance", "Accounting", "Driving", "Gardening", "0"},
            {"What is a story with magical events often called?", "Fantasy", "Manual", "Invoice", "Biography only", "0"},
            {"Which instrument is usually played by strumming strings?", "Guitar", "Flute", "Trumpet", "Drum", "0"},
            {"What do subtitles provide in a film?", "Written dialogue", "Extra seats", "Food prices", "Sound effects only", "0"},
            {"Which form of entertainment uses painted or digital images in motion?", "Animation", "Cycling", "Cooking", "Chess", "0"},
            {"What is a person who writes a screenplay called?", "Screenwriter", "Goalkeeper", "Conductor only", "Painter", "0"},
            {"Which audience activity follows a good performance?", "Applause", "Silence always", "Sleeping", "Leaving before it starts", "0"}
        }));
        return categories;
    }
}
