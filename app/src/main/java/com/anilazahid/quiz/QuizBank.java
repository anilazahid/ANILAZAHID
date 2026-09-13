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
            {"How many sides does a hexagon have?", "Six", "Five", "Seven", "Eight", "0"}
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
            {"What is the boiling point of water at sea level?", "100 C", "50 C", "75 C", "150 C", "0"}
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
            {"How many sides does a triangle have?", "3", "4", "5", "6", "0"}
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
            {"What is the comparative form of good?", "Better", "Best", "Gooder", "More good", "0"}
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
            {"What does RAM help a computer do?", "Temporarily store working data", "Print documents", "Connect to satellites", "Play sound only", "0"}
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
            {"The Roman Empire was centered around which city?", "Rome", "Athens", "Paris", "Cairo", "0"}
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
            {"What is the capital of Canada?", "Ottawa", "Toronto", "Vancouver", "Montreal", "0"}
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
            {"What is Pakistan's national sport traditionally recognized as?", "Field hockey", "Cricket", "Football", "Squash", "0"}
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
            {"What is the declaration of faith called?", "Shahadah", "Sawm", "Zakat", "Tawaf", "0"}
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
            {"Which country hosted the first modern Olympics?", "Greece", "France", "United Kingdom", "Italy", "0"}
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
            {"Which planet is known as the Red Planet?", "Mars", "Jupiter", "Venus", "Saturn", "0"}
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
            {"What should you do to cross a road safely?", "Look both ways", "Close your eyes", "Run without looking", "Stand in the road", "0"}
        }));
        return categories;
    }
}
