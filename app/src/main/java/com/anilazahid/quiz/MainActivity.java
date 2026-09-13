package com.anilazahid.quiz;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.ads.*;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference db;
    private GoogleSignInClient mGoogleSignInClient;
    
    // AdMob Variables
    private RewardedAd mRewardedAd;
    private InterstitialAd mInterstitialAd;
    private AdView mAdView;

    private View authView, mainAppView, pageHome, pageCategories, pageRank, pageProfile;
    private EditText authUser, authEmail, authPass;
    private Button btnAuthSubmit;
    private TextView btnToggleAuth, tvUserName, tvMainCoins, profName, profEmail, profCoins, dailyRewardStatus;
    private Button dailyRewardClaim;
    private ImageView imgUserAvatar, imgProfileAvatar, homeTrophy3D;
    private BottomNavigationView bottomNav;
    private RecyclerView recyclerCategories, recyclerTournamentsFull, recyclerLeaderboardFull;

    private View quizContainer, quizTopicBanner;
    private TextView quizHeader, quizPointsCurrent, tvQuestion, tvTimer, quizTopicIcon, quizTopicName;
    private Button[] btnOpts = new Button[4];
    private CountDownTimer countDownTimer;

    private boolean isLoginMode = true;
    private int userCoins = 100;
    private String userNameStr = "Player";

    private List<Tournament> tournamentList = new ArrayList<>();
    private List<User> leaderboardList = new ArrayList<>();
    private List<QuickCategory> quickCategoryList = new ArrayList<>();

    private int currentQIndex = 0;
    private int coinsEarnedInQuiz = 0;
    private int correctAnswersCount = 0;
    private Tournament activeTournament = null;
    private QuickCategory activeQuickCategory = null;
    private List<QuizQuestion> currentQuizQuestions = new ArrayList<>();
    private boolean dailyRewardClaimed;

    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Google Login Failed (Code: " + e.getStatusCode() + ")", Toast.LENGTH_LONG).show();
            }
        }
    );

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFirebase(); initGoogleAuth(); initViews(); setupListeners(); initAds();
        loadQuickCategoriesFromFirebase();
        if (auth.getCurrentUser() != null) showMainApp();
    }

    private void initFirebase() { auth = FirebaseAuth.getInstance(); db = FirebaseDatabase.getInstance().getReference(); }

    private void initGoogleAuth() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void initAds() {
        MobileAds.initialize(this, status -> {});

        // 1. Load Rewarded Ad
        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build(),
            new RewardedAdLoadCallback() {
                @Override public void onAdLoaded(@NonNull RewardedAd ad) { mRewardedAd = ad; }
                @Override public void onAdFailedToLoad(@NonNull LoadAdError e) { mRewardedAd = null; }
            });
            
        // 2. Load Banner Ad
        mAdView = findViewById(R.id.adView);
        if (mAdView != null) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
        
        // 3. Load Interstitial Ad
        loadInterstitialAd();
    }
    
    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest,
            new InterstitialAdLoadCallback() {
                @Override public void onAdLoaded(@NonNull InterstitialAd interstitialAd) { mInterstitialAd = interstitialAd; }
                @Override public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) { mInterstitialAd = null; }
            });
    }

    private void initViews() {
        authView = findViewById(R.id.authView); mainAppView = findViewById(R.id.mainAppView);
        pageHome = findViewById(R.id.pageHome); pageCategories = findViewById(R.id.pageCategories);
        pageRank = findViewById(R.id.pageRank); pageProfile = findViewById(R.id.pageProfile);
        bottomNav = findViewById(R.id.bottomNav);

        authUser = findViewById(R.id.authUsername); authEmail = findViewById(R.id.authEmail); authPass = findViewById(R.id.authPass);
        btnAuthSubmit = findViewById(R.id.btnAuthSubmit); btnToggleAuth = findViewById(R.id.btnToggleAuth);
        tvUserName = findViewById(R.id.tvUserName); tvMainCoins = findViewById(R.id.tvMainCoins);
        profName = findViewById(R.id.profName); profEmail = findViewById(R.id.profEmail); profCoins = findViewById(R.id.profCoins);
        dailyRewardStatus = findViewById(R.id.dailyRewardStatus); dailyRewardClaim = findViewById(R.id.dailyRewardClaim);
        imgUserAvatar = findViewById(R.id.imgUserAvatar); imgProfileAvatar = findViewById(R.id.imgProfileAvatar);
        homeTrophy3D = findViewById(R.id.homeTrophy3D);

        if (homeTrophy3D != null) {
            Animation pulseAnim = AnimationUtils.loadAnimation(this, R.anim.pulse_anim);
            homeTrophy3D.startAnimation(pulseAnim);
        }

        recyclerCategories = findViewById(R.id.recyclerCategories); recyclerCategories.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerTournamentsFull = findViewById(R.id.recyclerTournamentsFull); recyclerTournamentsFull.setLayoutManager(new LinearLayoutManager(this));
        recyclerLeaderboardFull = findViewById(R.id.recyclerLeaderboardFull); recyclerLeaderboardFull.setLayoutManager(new LinearLayoutManager(this));

        quizContainer = findViewById(R.id.kbcQuizContainer); quizHeader = findViewById(R.id.quizHeader);
        tvTimer = findViewById(R.id.tvTimer); quizPointsCurrent = findViewById(R.id.quizPointsCurrent); tvQuestion = findViewById(R.id.tvQuestion);
        quizTopicBanner = findViewById(R.id.quizTopicBanner); quizTopicIcon = findViewById(R.id.quizTopicIcon); quizTopicName = findViewById(R.id.quizTopicName);
        btnOpts[0] = findViewById(R.id.btnOpt1); btnOpts[1] = findViewById(R.id.btnOpt2); btnOpts[2] = findViewById(R.id.btnOpt3); btnOpts[3] = findViewById(R.id.btnOpt4);
    }

    private void setupListeners() {
        btnToggleAuth.setOnClickListener(v -> {
            isLoginMode = !isLoginMode; authUser.setVisibility(isLoginMode ? View.GONE : View.VISIBLE);
            btnAuthSubmit.setText(isLoginMode ? "LOGIN" : "REGISTER"); btnToggleAuth.setText(isLoginMode ? "New User? Register here" : "Already registered? Login");
        });
        btnAuthSubmit.setOnClickListener(v -> handleEmailAuth());
        findViewById(R.id.btnGoogleSignIn).setOnClickListener(v -> {
            mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> googleSignInLauncher.launch(mGoogleSignInClient.getSignInIntent()));
        });
        bottomNav.setOnItemSelectedListener(item -> {
            pageHome.setVisibility(item.getItemId() == R.id.nav_home ? View.VISIBLE : View.GONE);
            pageCategories.setVisibility(item.getItemId() == R.id.nav_categories ? View.VISIBLE : View.GONE);
            pageRank.setVisibility(item.getItemId() == R.id.nav_rank ? View.VISIBLE : View.GONE);
            pageProfile.setVisibility(item.getItemId() == R.id.nav_profile ? View.VISIBLE : View.GONE);
            if (item.getItemId() == R.id.nav_rank) loadLeaderboard();
            return true;
        });
        findViewById(R.id.btnProfileLogout).setOnClickListener(v -> logout());
        findViewById(R.id.btnOpenAdmin).setOnClickListener(v -> startActivity(new Intent(this, AdminLoginActivity.class)));
        findViewById(R.id.btnEarnMoreCoins).setOnClickListener(v -> {
            if (mRewardedAd != null) { mRewardedAd.show(this, reward -> { addCoins(50, "Video Ad"); Toast.makeText(this, "+50 Coins Claimed!", Toast.LENGTH_SHORT).show(); initAds(); }); } else { addCoins(10, "Free Claim"); Toast.makeText(this, "+10 Free Coins!", Toast.LENGTH_SHORT).show(); }
        });
        findViewById(R.id.btnDailyClaim).setOnClickListener(v -> claimDailyReward());
        if (dailyRewardClaim != null) dailyRewardClaim.setOnClickListener(v -> claimDailyReward());
        
        // Profile features listeners
        findViewById(R.id.btnEditProfile).setOnClickListener(v -> showEditProfileDialog());
        findViewById(R.id.menuAchievements).setOnClickListener(v -> showFeatureDialog("🏅 Achievements", "• Quiz Master (Unlocked)\n• Tournament Champ (Unlocked)\n• Streak Legend (5 Days Active)\n• High Roller (500+ Coins Earned)"));
        findViewById(R.id.menuRewards).setOnClickListener(v -> showFeatureDialog("🎁 Rewards Center", "• Daily Login Bonus: Active (+20 Coins)\n• Spin & Win: Available Every 4 Hours\n• Referral Bonus: +50 Coins per Friend"));
        findViewById(R.id.menuHistory).setOnClickListener(v -> showFeatureDialog("🕐 Match History", "• Quick Quiz: +40 Points (Won)\n• Tournament Arena: +150 Points (1st Place)\n• Quick Quiz: +10 Points (Completed)"));
        findViewById(R.id.menuReferEarn).setOnClickListener(v -> showFeatureDialog("👥 Refer & Earn", "Share your invite link with friends!\n\nYour Referral Code: JAVAGOAT2026\n\nEarn 50 coins instantly when your friend joins."));

        findViewById(R.id.btnCloseQuiz).setOnClickListener(v -> endQuiz());
        for (int i = 0; i < 4; i++) { final int ansIdx = i; btnOpts[i].setOnClickListener(v -> submitAnswer(ansIdx)); }
    }

    private void showFeatureDialog(String title, String message) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_result);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView dIcon = dialog.findViewById(R.id.dialogIcon);
        TextView dTitle = dialog.findViewById(R.id.dialogTitle);
        TextView dMsg = dialog.findViewById(R.id.dialogMessage);
        Button dBtn = dialog.findViewById(R.id.dialogBtn);
        dIcon.setText("🌟"); dTitle.setText(title); dMsg.setText(message);
        dBtn.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showEditProfileDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_result);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        
        LinearLayout container = (LinearLayout) ((ViewGroup) dialog.findViewById(R.id.dialogBtn).getParent());
        TextView dIcon = dialog.findViewById(R.id.dialogIcon);
        TextView dTitle = dialog.findViewById(R.id.dialogTitle);
        TextView dMsg = dialog.findViewById(R.id.dialogMessage);
        Button dBtn = dialog.findViewById(R.id.dialogBtn);

        dIcon.setText("✏️");
        dTitle.setText("Edit Profile");
        dMsg.setVisibility(View.GONE);

        EditText inputName = new EditText(this);
        inputName.setHint("Enter new username");
        inputName.setText(userNameStr);
        inputName.setTextColor(Color.WHITE);
        inputName.setHintTextColor(Color.parseColor("#A79BC0"));
        inputName.setBackgroundResource(R.drawable.bg_input);
        inputName.setPadding(20, 20, 20, 20);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 24);
        inputName.setLayoutParams(params);

        container.addView(inputName, 2);

        dBtn.setText("SAVE CHANGES");
        dBtn.setOnClickListener(v -> {
            String newName = inputName.getText().toString().trim();
            if(!newName.isEmpty() && auth.getUid() != null) {
                db.child("users").child(auth.getUid()).child("username").setValue(newName);
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });
        dialog.show();
    }

    private void handleEmailAuth() {
        String e = authEmail.getText().toString().trim(), p = authPass.getText().toString().trim();
        if (e.isEmpty() || p.isEmpty()) return;
        if (isLoginMode) {
            auth.signInWithEmailAndPassword(e, p).addOnSuccessListener(res -> showMainApp()).addOnFailureListener(err -> Toast.makeText(this, err.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            String u = authUser.getText().toString().trim(); if (u.isEmpty()) return;
            auth.createUserWithEmailAndPassword(e, p).addOnSuccessListener(res -> {
                db.child("users").child(res.getUser().getUid()).setValue(new User(u, e, "", 100)); showMainApp();
            }).addOnFailureListener(err -> Toast.makeText(this, err.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential).addOnSuccessListener(authResult -> {
            FirebaseUser user = authResult.getUser();
            if (user != null) {
                String picUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "";
                db.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot s) {
                        if (!s.exists()) db.child("users").child(user.getUid()).setValue(new User(user.getDisplayName() != null ? user.getDisplayName() : "Player", user.getEmail() != null ? user.getEmail() : "", picUrl, 100));
                        else if (picUrl.length() > 0) db.child("users").child(user.getUid()).child("photoUrl").setValue(picUrl);
                        showMainApp();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Auth Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void logout() {
        auth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> returnToLogin());
    }

    private void returnToLogin() {
        mainAppView.setVisibility(View.GONE);
        authView.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

    private void showMainApp() {
        authView.setVisibility(View.GONE); mainAppView.setVisibility(View.VISIBLE);
        bottomNav.setSelectedItemId(R.id.nav_home);
        loadUserData(); loadTournaments(); loadLeaderboard();
    }

    private void loadUserData() {
        if (auth.getUid() == null) return;
        db.child("users").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot s) {
                if (!s.exists()) return;
                userNameStr = s.child("username").getValue(String.class);
                String email = s.child("email").getValue(String.class);
                String picUrl = s.child("photoUrl").getValue(String.class);
                Integer pts = s.child("points").getValue(Integer.class);
                userCoins = pts != null ? pts : 100;
                updateDailyRewardState(s.child("dailyRewardDate").getValue(String.class));

                tvUserName.setText("Hello, " + (userNameStr != null ? userNameStr : "Player") + " 👋"); 
                tvMainCoins.setText(String.valueOf(userCoins));
                profName.setText(userNameStr != null ? userNameStr : "Player"); 
                profEmail.setText(email != null ? email : "");
                profCoins.setText("🪙 " + userCoins);

                if (picUrl != null && !picUrl.isEmpty() && !isDestroyed()) {
                    Glide.with(MainActivity.this).load(picUrl).circleCrop().into(imgUserAvatar);
                    Glide.with(MainActivity.this).load(picUrl).circleCrop().into(imgProfileAvatar);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }

    private void addCoins(int amount, String desc) { 
        if(auth.getUid() != null) {
            db.child("users").child(auth.getUid()).child("points").setValue(userCoins + amount); 
        }
    }

    private String todayKey() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }

    private void updateDailyRewardState(String claimedDate) {
        dailyRewardClaimed = todayKey().equals(claimedDate);
        String label = dailyRewardClaimed ? "CLAIMED TODAY" : "CLAIM DAILY REWARD (+20)";
        if (dailyRewardStatus != null) dailyRewardStatus.setText(dailyRewardClaimed ? "Daily reward claimed. Come back tomorrow." : "Claim 20 coins once every day.");
        if (dailyRewardClaim != null) {
            dailyRewardClaim.setText(label);
            dailyRewardClaim.setEnabled(!dailyRewardClaimed);
            dailyRewardClaim.setBackgroundResource(dailyRewardClaimed ? R.drawable.bg_card : R.drawable.bg_btn_gold);
        }
        Button profileClaim = findViewById(R.id.btnDailyClaim);
        profileClaim.setText(label);
        profileClaim.setEnabled(!dailyRewardClaimed);
    }

    private void claimDailyReward() {
        if (auth.getUid() == null || dailyRewardClaimed) {
            Toast.makeText(this, "Daily reward already claimed today.", Toast.LENGTH_SHORT).show();
            return;
        }
        String today = todayKey();
        DatabaseReference rewardDate = db.child("users").child(auth.getUid()).child("dailyRewardDate");
        rewardDate.runTransaction(new Transaction.Handler() {
            @NonNull @Override public Transaction.Result doTransaction(MutableData currentData) {
                if (today.equals(currentData.getValue(String.class))) return Transaction.abort();
                currentData.setValue(today);
                return Transaction.success(currentData);
            }
            @Override public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                if (error != null || !committed) {
                    if (!committed) {
                        dailyRewardClaimed = true;
                        updateDailyRewardState(today);
                        Toast.makeText(MainActivity.this, "Daily reward already claimed today.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Could not claim daily reward. Try again.", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                dailyRewardClaimed = true;
                addCoins(QuizBank.DAILY_REWARD_COINS, "Daily Bonus");
                updateDailyRewardState(today);
                Toast.makeText(MainActivity.this, "+20 Daily Coins Added!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTournaments() {
        db.child("tournaments").addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot s) {
                tournamentList.clear();
                if (s.exists()) {
                    for (DataSnapshot ds : s.getChildren()) {
                        Tournament t = ds.getValue(Tournament.class);
                        if (t != null && t.entry_points > 0) {
                            t.id = ds.getKey();
                            t.playedCount = (int) ds.child("players").getChildrenCount();
                            t.hasPlayed = auth.getUid() != null && ds.child("players").hasChild(auth.getUid());
                            tournamentList.add(t);
                        }
                    }
                }
                recyclerTournamentsFull.setAdapter(new TournamentFullAdapter(tournamentList));
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }

    private void loadQuickCategoriesFromFirebase() {
        db.child("quick_categories").addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snap) {
                quickCategoryList.clear();
                List<QuizBank.StarterCategory> starterCategories = QuizBank.categories();
                Set<String> existingTitles = new HashSet<>();
                if (snap.exists()) {
                    for (DataSnapshot dSnap : snap.getChildren()) {
                        String title = dSnap.child("title").getValue(String.class);
                        String icon = dSnap.child("icon").getValue(String.class);
                        existingTitles.add(title != null ? title : "");
                        List<QuizQuestion> qList = new ArrayList<>();
                        DataSnapshot qSnap = dSnap.child("questions");
                        if (qSnap.exists()) {
                            for (DataSnapshot qs : qSnap.getChildren()) {
                                String q = qs.child("q").getValue(String.class);
                                String o1 = qs.child("opt1").getValue(String.class);
                                String o2 = qs.child("opt2").getValue(String.class);
                                String o3 = qs.child("opt3").getValue(String.class);
                                String o4 = qs.child("opt4").getValue(String.class);
                                Integer ans = qs.child("ansIdx").getValue(Integer.class);
                                if (ans == null) ans = answerIndex(qs.child("correctAnswer").getValue(String.class));
                                if(q != null && o1 != null && o2 != null && o3 != null && o4 != null) qList.add(new QuizQuestion(qs.getKey(), q, o1, o2, o3, o4, ans != null ? ans : 0, 10));
                            }
                        }
                        QuizBank.StarterCategory matchingStarter = findStarterCategory(starterCategories, title);
                        if (qList.isEmpty() && matchingStarter != null) {
                            qList.addAll(starterQuestions(matchingStarter));
                            seedQuestions(dSnap.getKey(), matchingStarter);
                        }
                        while (qList.size() < 10) qList.add(new QuizQuestion((title != null ? title : "Quiz") + " Question " + (qList.size() + 1) + "?", "Option A", "Option B", "Option C", "Option D", 0, 10));
                        quickCategoryList.add(new QuickCategory(dSnap.getKey(), title != null ? title : "General", icon != null && !icon.isEmpty() ? icon : "🧩", qList));
                    }
                }
                List<QuizBank.StarterCategory> missing = new ArrayList<>();
                for (QuizBank.StarterCategory starter : starterCategories) {
                    if (!existingTitles.contains(starter.title)) {
                        quickCategoryList.add(new QuickCategory(starterKey(starter.title), starter.title, starter.icon, starterQuestions(starter)));
                        missing.add(starter);
                    }
                }
                if (!missing.isEmpty()) seedStarterCategories(missing);
                recyclerCategories.setAdapter(new QuickCategoryAdapter(quickCategoryList));
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private int answerIndex(String answer) {
        if (answer == null || answer.isEmpty()) return 0;
        return Math.max(0, Math.min(3, Character.toUpperCase(answer.charAt(0)) - 'A'));
    }

    private QuizBank.StarterCategory findStarterCategory(List<QuizBank.StarterCategory> categories, String title) {
        if (title == null) return null;
        for (QuizBank.StarterCategory category : categories) if (category.title.equals(title)) return category;
        return null;
    }

    private List<QuizQuestion> starterQuestions(QuizBank.StarterCategory category) {
        List<QuizQuestion> questions = new ArrayList<>();
        for (int index = 0; index < category.questions.length; index++) {
            String[] item = category.questions[index];
            questions.add(new QuizQuestion(starterKey(category.title) + "-q" + index, item[0], item[1], item[2], item[3], item[4], Integer.parseInt(item[5]), 10));
        }
        return questions;
    }

    private Map<String, Object> starterQuestionData(String[] item) {
        Map<String, Object> data = new HashMap<>();
        data.put("q", item[0]); data.put("opt1", item[1]); data.put("opt2", item[2]);
        data.put("opt3", item[3]); data.put("opt4", item[4]); data.put("ansIdx", Integer.parseInt(item[5]));
        data.put("correctAnswer", String.valueOf((char) ('A' + Integer.parseInt(item[5])))); data.put("points", 10);
        return data;
    }

    private void seedQuestions(String categoryKey, QuizBank.StarterCategory category) {
        if (categoryKey == null) return;
        for (String[] item : category.questions) db.child("quick_categories").child(categoryKey).child("questions").push().setValue(starterQuestionData(item));
    }

    private void seedStarterCategories(List<QuizBank.StarterCategory> categories) {
        for (QuizBank.StarterCategory category : categories) {
            DatabaseReference ref = db.child("quick_categories").push();
            Map<String, Object> data = new HashMap<>(); data.put("title", category.title); data.put("icon", category.icon);
            Map<String, Object> questions = new HashMap<>();
            for (String[] item : category.questions) questions.put(ref.push().getKey(), starterQuestionData(item));
            data.put("questions", questions); ref.setValue(data);
        }
    }

    private void loadLeaderboard() {
        db.child("users").orderByChild("points").limitToLast(50).addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot s) {
                leaderboardList.clear();
                for (DataSnapshot ds : s.getChildren()) { User u = ds.getValue(User.class); if (u != null) leaderboardList.add(0, u); }
                recyclerLeaderboardFull.setAdapter(new LeaderboardAdapter(leaderboardList));
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }

    private void startQuiz(Tournament t) {
        if (t.hasPlayed) { Toast.makeText(this, "You already completed this pool!", Toast.LENGTH_SHORT).show(); return; }
        if (t.pool_size > 0 && t.playedCount >= t.pool_size) { Toast.makeText(this, "This pool is full!", Toast.LENGTH_SHORT).show(); return; }
        if (t.entry_points > userCoins) { Toast.makeText(this, "Not enough coins to join pool!", Toast.LENGTH_SHORT).show(); return; }

        activeTournament = t; activeQuickCategory = null;
        addCoins(-t.entry_points, "Tournament Entry Fee");
        Toast.makeText(this, "Deducted " + t.entry_points + " Entry Coins", Toast.LENGTH_SHORT).show();
        currentQuizQuestions.clear();

        if (t.id != null) {
            db.child("tournaments").child(t.id).child("questions").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override public void onDataChange(@NonNull DataSnapshot snap) {
                    if (snap.exists()) {
                        for (DataSnapshot qSnap : snap.getChildren()) {
                            String q = qSnap.child("q").getValue(String.class);
                            String o1 = qSnap.child("opt1").getValue(String.class); String o2 = qSnap.child("opt2").getValue(String.class);
                            String o3 = qSnap.child("opt3").getValue(String.class); String o4 = qSnap.child("opt4").getValue(String.class);
                            Integer ans = qSnap.child("ansIdx").getValue(Integer.class);
                            Integer pts = qSnap.child("points").getValue(Integer.class);
                            if (q != null && o1 != null) currentQuizQuestions.add(new QuizQuestion(qSnap.getKey(), q, o1, o2, o3, o4, ans != null ? ans : 0, pts != null ? pts : 50));
                        }
                    }
                    if(currentQuizQuestions.isEmpty()) currentQuizQuestions.add(new QuizQuestion("Tournament Question 1?", "A", "B", "C", "D", 0, 50));
                    launchQuizUI();
                }
                @Override public void onCancelled(@NonNull DatabaseError error) { launchQuizUI(); }
            });
        }
    }

    private void startQuickQuiz(QuickCategory qc) {
        activeTournament = null; activeQuickCategory = qc;
        if (auth.getUid() == null) {
            prepareQuickQuestions(qc, new HashSet<>());
            return;
        }
        db.child("users").child(auth.getUid()).child("answeredQuestions").child(historyKey(qc.id))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Set<String> answered = new HashSet<>();
                        for (DataSnapshot item : snapshot.getChildren()) answered.add(item.getKey());
                        prepareQuickQuestions(qc, answered);
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) { prepareQuickQuestions(qc, new HashSet<>()); }
                });
    }

    private void prepareQuickQuestions(QuickCategory category, Set<String> answered) {
        List<QuizQuestion> unanswered = new ArrayList<>();
        List<QuizQuestion> all = new ArrayList<>(category.questions);
        for (QuizQuestion question : all) if (!answered.contains(historyKey(question.id))) unanswered.add(question);
        Collections.shuffle(unanswered);
        if (unanswered.size() < 10) {
            List<QuizQuestion> cycle = new ArrayList<>(all);
            Collections.shuffle(cycle);
            for (QuizQuestion question : cycle) if (!containsQuestion(unanswered, question.id)) unanswered.add(question);
        }
        currentQuizQuestions.clear();
        int questionCount = Math.min(10, unanswered.size());
        for (int index = 0; index < questionCount; index++) currentQuizQuestions.add(unanswered.get(index).copyWithShuffledOptions());
        launchQuizUI();
    }

    private boolean containsQuestion(List<QuizQuestion> questions, String id) {
        for (QuizQuestion question : questions) if (Objects.equals(question.id, id)) return true;
        return false;
    }

    private String starterKey(String title) {
        return "starter-" + title.toLowerCase(Locale.US).replaceAll("[^a-z0-9]+", "-");
    }

    private String historyKey(String value) {
        if (value == null || value.isEmpty()) return "unknown";
        return value.replace(".", "_").replace("#", "_").replace("$", "_").replace("[", "_").replace("]", "_").replace("/", "_");
    }

    private void launchQuizUI() {
        quizContainer.setVisibility(View.VISIBLE);
        currentQIndex = 0; coinsEarnedInQuiz = 0; correctAnswersCount = 0;
        if (activeQuickCategory != null) {
            quizTopicIcon.setText(activeQuickCategory.icon);
            quizTopicName.setText(activeQuickCategory.title);
            quizTopicBanner.setVisibility(View.VISIBLE);
        } else {
            quizTopicBanner.setVisibility(View.GONE);
        }
        loadNextQuestion();
    }

    private void loadNextQuestion() {
        if (currentQIndex >= currentQuizQuestions.size()) { endQuiz(); return; }
        if (countDownTimer != null) countDownTimer.cancel();
        for (Button b : btnOpts) {
            b.setBackgroundResource(R.drawable.bg_option_btn);
            b.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            b.setEnabled(true);
        }
        QuizQuestion q = currentQuizQuestions.get(currentQIndex);
        quizHeader.setText("Question " + (currentQIndex + 1) + "/" + currentQuizQuestions.size());
        quizPointsCurrent.setText(activeTournament == null ? ("Winning: " + coinsEarnedInQuiz + " Coins") : "🏆 Tournament Match Live");
        tvQuestion.setText(q.q);
        btnOpts[0].setText("A. " + q.opts[0]); btnOpts[1].setText("B. " + q.opts[1]);
        btnOpts[2].setText("C. " + q.opts[2]); btnOpts[3].setText("D. " + q.opts[3]);
        countDownTimer = new CountDownTimer(15000, 1000) {
            public void onTick(long millis) { tvTimer.setText("⏳ " + (millis / 1000) + "s"); }
            public void onFinish() { submitAnswer(-1); }
        }.start();
    }

    private void setOptionColor(Button btn, int bgColor, int strokeColor, int textColor) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(ContextCompat.getColor(this, bgColor));
        gd.setCornerRadius(48f);
        gd.setStroke(4, ContextCompat.getColor(this, strokeColor));
        btn.setBackground(gd);
        btn.setTextColor(ContextCompat.getColor(this, textColor));
    }

    private void submitAnswer(int idx) {
        if (countDownTimer != null) countDownTimer.cancel();
        for (Button b : btnOpts) b.setEnabled(false);
        QuizQuestion q = currentQuizQuestions.get(currentQIndex);
        if (activeQuickCategory != null && auth.getUid() != null && q.id != null) {
            db.child("users").child(auth.getUid()).child("answeredQuestions")
                .child(historyKey(activeQuickCategory.id)).child(historyKey(q.id)).setValue(true);
        }
        if (idx >= 0) {
            setOptionColor(btnOpts[idx], R.color.quiz_option_selected, R.color.gold, R.color.gold_text_dark);
        }
        if (idx == q.ansIdx) {
            if (idx >= 0) setOptionColor(btnOpts[idx], R.color.correct_answer, R.color.correct_answer, R.color.text_primary);
            correctAnswersCount++;
            if (activeTournament == null) coinsEarnedInQuiz += q.points;
        } else {
            if (idx >= 0) setOptionColor(btnOpts[idx], R.color.wrong_answer, R.color.wrong_answer, R.color.text_primary);
            setOptionColor(btnOpts[q.ansIdx], R.color.correct_answer, R.color.correct_answer, R.color.text_primary);
        }
        new Handler().postDelayed(() -> { currentQIndex++; loadNextQuestion(); }, 1500);
    }

    private void showResultDialog(String title, String message, String emoji) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_result);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        TextView dIcon = dialog.findViewById(R.id.dialogIcon);
        TextView dTitle = dialog.findViewById(R.id.dialogTitle);
        TextView dMsg = dialog.findViewById(R.id.dialogMessage);
        Button dBtn = dialog.findViewById(R.id.dialogBtn);
        dIcon.setText(emoji); dTitle.setText(title); dMsg.setText(message);
        dBtn.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void endQuiz() {
        if (countDownTimer != null) countDownTimer.cancel();
        quizContainer.setVisibility(View.GONE);
        
        // --- Show Interstitial Ad When Quiz Ends ---
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
            loadInterstitialAd(); // Load the next one in the background
        }
        
        int totalQ = currentQuizQuestions.size();
        recordQuizResult(totalQ);
        if (activeTournament != null) {
            if(auth.getUid() != null) {
                db.child("tournaments").child(activeTournament.id).child("players").child(auth.getUid()).setValue(correctAnswersCount);
            }
            float winRatio = (float) correctAnswersCount / totalQ;
            int totalPool = (activeTournament.pool_size > 0) ? (activeTournament.pool_size * activeTournament.entry_points) : activeTournament.reward_points;
            int prize = 0; String rank = "";
            if (winRatio == 1.0f) { prize = (int) (totalPool * 0.5); rank = "1st Place"; }
            else if (winRatio >= 0.75f) { prize = (int) (totalPool * 0.3); rank = "2nd Place"; }
            else if (winRatio >= 0.5f) { prize = (int) (totalPool * 0.2); rank = "3rd Place"; }
            if (prize > 0) {
                addCoins(prize, "Tournament Prize");
                showResultDialog(rank + " Winner!", "You scored " + correctAnswersCount + "/" + totalQ + "!\n\nYou won " + prize + " Coins from the pool!", "🏆");
            } else {
                showResultDialog("Match Finished", "You scored " + correctAnswersCount + "/" + totalQ + ".\n\nBetter luck next time!", "💔");
            }
        } else {
            if (coinsEarnedInQuiz > 0) {
                addCoins(coinsEarnedInQuiz, "Quick Quiz Reward");
                showResultDialog("Good Game!", "You scored " + correctAnswersCount + "/" + totalQ + ".\nEarned " + coinsEarnedInQuiz + " Points!", "🎉");
            } else {
                showResultDialog("Game Over", "You scored " + correctAnswersCount + "/" + totalQ + ".\nPractice more!", "📚");
            }
        }
    }

    private void recordQuizResult(int totalQuestions) {
        if (auth.getCurrentUser() == null || totalQuestions <= 0) return;
        Map<String, Object> result = new HashMap<>();
        result.put("userId", auth.getUid());
        result.put("userName", userNameStr);
        result.put("email", auth.getCurrentUser().getEmail());
        result.put("category", activeQuickCategory != null ? activeQuickCategory.title : (activeTournament != null ? activeTournament.title : "Quiz"));
        result.put("score", coinsEarnedInQuiz);
        result.put("correctAnswers", correctAnswersCount);
        result.put("totalQuestions", totalQuestions);
        result.put("timestamp", ServerValue.TIMESTAMP);
        db.child("quiz_results").push().setValue(result);
    }

    class QuickCategoryAdapter extends RecyclerView.Adapter<QuickCategoryAdapter.VH> {
        List<QuickCategory> list; QuickCategoryAdapter(List<QuickCategory> l) { list = l; }
        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) { return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_category, p, false)); }
        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            QuickCategory qc = list.get(pos);
            h.tTitle.setText(qc.title);
            h.tIcon.setText(qc.icon != null && !qc.icon.isEmpty() ? qc.icon : "🧩");
            final int[] tiles = {R.drawable.bg_tile_purple, R.drawable.bg_tile_blue, R.drawable.bg_tile_gold, R.drawable.bg_tile_red};
            h.bg.setBackgroundResource(tiles[pos % tiles.length]);
            h.itemView.setOnClickListener(v -> startQuickQuiz(qc));
        }
        @Override public int getItemCount() { return list.size(); }
        class VH extends RecyclerView.ViewHolder { TextView tTitle, tIcon; View bg; VH(View v) { super(v); tTitle = v.findViewById(R.id.catTitle); tIcon = v.findViewById(R.id.catIcon); bg = v.findViewById(R.id.cardCategoryBg); } }
    }

    class TournamentFullAdapter extends RecyclerView.Adapter<TournamentFullAdapter.VH> {
        List<Tournament> list; TournamentFullAdapter(List<Tournament> l) { list = l; }
        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) { return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_tournament, p, false)); }
        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            Tournament t = list.get(pos); h.tTitle.setText(t.title);
            h.tDetails.setText("Entry: " + t.entry_points + " 🪙 | Players: " + t.playedCount + (t.pool_size > 0 ? ("/" + t.pool_size) : ""));
            
            // 3D Realistic Golden Cup pulse animation
            Animation pulse = AnimationUtils.loadAnimation(h.itemView.getContext(), R.anim.pulse_anim);
            h.tCupImage.startAnimation(pulse);

            if (t.hasPlayed) {
                h.btnPlay.setText("PLAYED"); h.btnPlay.setEnabled(false); h.btnPlay.setBackgroundColor(0xFF3B2A5E); h.btnPlay.setTextColor(0xFF7A6B99);
            } else if (t.pool_size > 0 && t.playedCount >= t.pool_size) {
                h.btnPlay.setText("FULL"); h.btnPlay.setEnabled(false); h.btnPlay.setBackgroundColor(0xFF3B2A5E); h.btnPlay.setTextColor(0xFF7A6B99);
            } else {
                h.btnPlay.setText("JOIN"); h.btnPlay.setEnabled(true); h.btnPlay.setBackgroundResource(R.drawable.bg_btn_gold); h.btnPlay.setTextColor(0xFF2A1B00);
            }
            h.btnPlay.setOnClickListener(v -> startQuiz(t));
        }
        @Override public int getItemCount() { return list.size(); }
        class VH extends RecyclerView.ViewHolder { TextView tTitle, tDetails; ImageView tCupImage; Button btnPlay; VH(View v) { super(v); tTitle = v.findViewById(R.id.tTitle); tDetails = v.findViewById(R.id.tDetails); tCupImage = v.findViewById(R.id.tCupImage); btnPlay = v.findViewById(R.id.btnPlayTournament); } }
    }

    class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.VH> {
        List<User> list; LeaderboardAdapter(List<User> l) { list = l; }
        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) { return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_leaderboard, p, false)); }
        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            User u = list.get(pos);
            h.lbRank.setText("#" + (pos + 1));
            if (pos == 0) h.lbRank.setTextColor(0xFFFFC93C);
            else if (pos == 1) h.lbRank.setTextColor(0xFFC0C0C0);
            else if (pos == 2) h.lbRank.setTextColor(0xFFCD7F32);
            else h.lbRank.setTextColor(0xFFA79BC0);
            h.lbName.setText(u.username != null ? u.username : "Player");
            h.lbScore.setText("🪙 " + u.points);
        }
        @Override public int getItemCount() { return list.size(); }
        class VH extends RecyclerView.ViewHolder { TextView lbRank, lbName, lbScore; VH(View v) { super(v); lbRank = v.findViewById(R.id.lbRank); lbName = v.findViewById(R.id.lbName); lbScore = v.findViewById(R.id.lbScore); } }
    }

    public static class User { public String username, email, photoUrl; public int points; public User(){} public User(String u, String e, String pic, int p){username=u; email=e; photoUrl=pic; points=p;} }
    public static class Tournament { public String id, title, status, icon; public int entry_points, reward_points, pool_size, playedCount; public boolean hasPlayed; public Tournament(){} }
    class QuizQuestion {
        String id, q; String[] opts = new String[4]; int ansIdx, points;
        QuizQuestion(String qu, String a, String b, String c, String d, int ans, int pts) { this(null, qu, a, b, c, d, ans, pts); }
        QuizQuestion(String questionId, String qu, String a, String b, String c, String d, int ans, int pts) {
            id = questionId; q = qu; opts[0] = a; opts[1] = b; opts[2] = c; opts[3] = d; ansIdx = ans; points = pts;
        }
        QuizQuestion copyWithShuffledOptions() {
            List<Integer> order = Arrays.asList(0, 1, 2, 3);
            Collections.shuffle(order);
            return new QuizQuestion(id, q, opts[order.get(0)], opts[order.get(1)], opts[order.get(2)], opts[order.get(3)], order.indexOf(ansIdx), points);
        }
    }
    class QuickCategory { String id, title, icon; List<QuizQuestion> questions; QuickCategory(String t, String i, List<QuizQuestion> q){this(null, t, i, q);} QuickCategory(String categoryId, String t, String i, List<QuizQuestion> q){id=categoryId; title=t; icon=i; questions=q;} }
}
