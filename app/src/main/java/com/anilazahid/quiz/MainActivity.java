package com.anilazahid.quiz;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference db;
    private GoogleSignInClient mGoogleSignInClient;
    
    // AdMob Variables
    private RewardedAd mRewardedAd;
    private InterstitialAd mInterstitialAd;
    private AdView mAdView;
    private AdView quizAdView;

    private View authView, mainAppView, pageHome, pageCategories, pageRank, pageProfile;
    private EditText authUser, authEmail, authPass;
    private Button btnAuthSubmit;
    private TextView btnToggleAuth, tvUserName, tvMainCoins, profName, profEmail, profCoins, dailyRewardStatus;
    private Button dailyRewardClaim;
    private ImageView imgUserAvatar, imgProfileAvatar, homeTrophy3D;
    private BottomNavigationView bottomNav;
    private RecyclerView recyclerCategories, recyclerTournamentsFull, recyclerTournamentsUpcoming, recyclerTournamentsCompleted, recyclerLeaderboardFull;
    private ProgressBar categoriesProgress, tournamentsProgress, leaderboardProgress;
    private TextView tournamentsEmpty, upcomingEmpty;

    private View quizContainer, quizTopicBanner;
    private TextView quizHeader, quizPointsCurrent, tvQuestion, tvTimer, quizTopicIcon, quizTopicName;
    private Button[] btnOpts = new Button[4];
    private CountDownTimer countDownTimer;
    private boolean authInProgress;

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
    private boolean dailyRewardInProgress;
    private String dailyRewardFeedback;
    private boolean adRewardInProgress;
    private boolean userDataLoaded;
    private boolean tournamentsLoaded;
    private boolean leaderboardLoaded;
    private boolean quickCategoriesLoaded;
    private boolean userDataLoading;
    private boolean tournamentsLoading;
    private boolean leaderboardLoading;
    private boolean quickCategoriesLoading;
    private boolean quizActive;
    private boolean answerLocked;
    private String quizAttemptId;

    private final ActivityResultLauncher<String[]> profilePicturePicker = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri == null || auth.getUid() == null) return;
                try {
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (SecurityException ignored) {
                }
                String persistentPath = copyProfilePicture(uri);
                if (persistentPath == null) {
                    Toast.makeText(this, "Could not save profile picture.", Toast.LENGTH_SHORT).show();
                    return;
                }
                displayProfilePicture(persistentPath);
                db.child("users").child(auth.getUid()).child("photoUrl").setValue(persistentPath)
                        .addOnFailureListener(error -> Toast.makeText(this, "Could not save profile picture.", Toast.LENGTH_SHORT).show());
            });

    private static final int WATCH_AD_REWARD_COINS = 50;

    private interface RewardClaimCallback {
        void onComplete(boolean committed, boolean alreadyClaimed, DatabaseError error);
    }

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
        loadRewardedAd();
            
        // 2. Load Banner Ad
        mAdView = findViewById(R.id.adView);
        if (mAdView != null) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
        quizAdView = findViewById(R.id.quizAdView);
        if (quizAdView != null) quizAdView.loadAd(new AdRequest.Builder().build());
        
        // 3. Load Interstitial Ad
        loadInterstitialAd();
    }

    private void loadRewardedAd() {
        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build(),
            new RewardedAdLoadCallback() {
                @Override public void onAdLoaded(@NonNull RewardedAd ad) { mRewardedAd = ad; }
                @Override public void onAdFailedToLoad(@NonNull LoadAdError e) { mRewardedAd = null; }
            });
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
        recyclerTournamentsUpcoming = findViewById(R.id.recyclerTournamentsUpcoming); recyclerTournamentsUpcoming.setLayoutManager(new LinearLayoutManager(this));
        recyclerTournamentsCompleted = findViewById(R.id.recyclerTournamentsCompleted); recyclerTournamentsCompleted.setLayoutManager(new LinearLayoutManager(this));
        recyclerLeaderboardFull = findViewById(R.id.recyclerLeaderboardFull); recyclerLeaderboardFull.setLayoutManager(new LinearLayoutManager(this));
        categoriesProgress = findViewById(R.id.categoriesLoading);
        tournamentsProgress = findViewById(R.id.tournamentsLoading);
        tournamentsEmpty = findViewById(R.id.tournamentsEmpty);
        upcomingEmpty = findViewById(R.id.upcomingTournamentsEmpty);
        leaderboardProgress = findViewById(R.id.leaderboardLoading);

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
            if (item.getItemId() == R.id.nav_categories && !tournamentsLoaded && !tournamentsLoading) loadTournaments();
            if (item.getItemId() == R.id.nav_rank && !leaderboardLoaded) loadLeaderboard();
            return true;
        });
        findViewById(R.id.btnProfileLogout).setOnClickListener(v -> logout());
        findViewById(R.id.btnOpenAdmin).setOnClickListener(v -> startActivity(new Intent(this, AdminLoginActivity.class)));
        findViewById(R.id.btnEarnMoreCoins).setOnClickListener(v -> {
            claimAdReward();
        });
        findViewById(R.id.btnDailyClaim).setOnClickListener(v -> claimDailyReward());
        if (dailyRewardClaim != null) dailyRewardClaim.setOnClickListener(v -> claimDailyReward());
        
        // Profile features listeners
        findViewById(R.id.btnEditProfile).setOnClickListener(v -> showEditProfileDialog());
        findViewById(R.id.menuAchievements).setOnClickListener(v -> showFeatureDialog("🏅 Achievements", "• Quiz Master (Unlocked)\n• Tournament Champ (Unlocked)\n• Streak Legend (5 Days Active)\n• High Roller (500+ Coins Earned)"));
        findViewById(R.id.menuRewards).setOnClickListener(v -> showFeatureDialog("🎁 Rewards Center", "• Daily Login Bonus: Active (+20 Coins)\n• Spin & Win: Available Every 4 Hours\n• Referral Bonus: +50 Coins per Friend"));
        findViewById(R.id.menuHistory).setOnClickListener(v -> showFeatureDialog("🕐 Match History", "• Quick Quiz: +40 Points (Won)\n• Tournament Arena: +150 Points (1st Place)\n• Quick Quiz: +10 Points (Completed)"));
        findViewById(R.id.menuReferEarn).setOnClickListener(v -> showFeatureDialog("👥 Refer & Earn", "Join Quiz With Anila Zahid and start your quiz journey!\n\nShare this app with friends for safe in-app rewards."));

        findViewById(R.id.btnCloseQuiz).setOnClickListener(v -> exitQuiz());
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

        Button choosePicture = new Button(this);
        choosePicture.setText("CHOOSE PICTURE");
        choosePicture.setOnClickListener(v -> profilePicturePicker.launch(new String[]{"image/*"}));
        Button removePicture = new Button(this);
        removePicture.setText("REMOVE PICTURE");
        removePicture.setOnClickListener(v -> removeProfilePicture());

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
        container.addView(choosePicture, 3);
        container.addView(removePicture, 4);

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
        if (authInProgress) return;
        String e = authEmail.getText().toString().trim(), p = authPass.getText().toString().trim();
        if (e.isEmpty() || p.isEmpty()) return;
        authInProgress = true;
        btnAuthSubmit.setEnabled(false);
        btnAuthSubmit.setText(isLoginMode ? "SIGNING IN..." : "CREATING...");
        if (isLoginMode) {
            auth.signInWithEmailAndPassword(e, p).addOnSuccessListener(res -> { finishAuth(true, null); showMainApp(); }).addOnFailureListener(err -> finishAuth(false, err.getMessage()));
        } else {
            String u = authUser.getText().toString().trim();
            if (u.isEmpty()) { finishAuth(false, "Enter a username."); return; }
            auth.createUserWithEmailAndPassword(e, p).addOnSuccessListener(res -> {
                db.child("users").child(res.getUser().getUid()).setValue(new User(u, e, "", 100))
                        .addOnSuccessListener(ignored -> { finishAuth(true, null); showMainApp(); }).addOnFailureListener(err -> finishAuth(false, err.getMessage()));
            }).addOnFailureListener(err -> finishAuth(false, err.getMessage()));
        }
    }

    private void finishAuth(boolean success, String message) {
        authInProgress = false;
        btnAuthSubmit.setEnabled(true);
        btnAuthSubmit.setText(isLoginMode ? "LOGIN" : "REGISTER");
        if (!success && message != null) Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
        userDataLoaded = false;
        tournamentsLoaded = false;
        leaderboardLoaded = false;
        quickCategoriesLoaded = false;
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
        if (!userDataLoaded && !userDataLoading) loadUserData();
        if (!quickCategoriesLoaded && !quickCategoriesLoading) loadQuickCategoriesFromFirebase();
    }

    private void loadUserData() {
        if (auth.getUid() == null) return;
        userDataLoading = true;
        db.child("users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot s) {
                userDataLoading = false;
                if (!s.exists()) return;
            userDataLoaded = true;
                userNameStr = s.child("username").getValue(String.class);
                String email = s.child("email").getValue(String.class);
                String picUrl = s.child("photoUrl").getValue(String.class);
                Integer pts = s.child("points").getValue(Integer.class);
                userCoins = pts != null ? pts : 100;
                updateDailyRewardState(s.child("dailyRewardDate").getValue(String.class));

                tvUserName.setText("Hello, " + (userNameStr != null ? userNameStr : "Player") + " 👋");
                updateCoinViews();
                profName.setText(userNameStr != null ? userNameStr : "Player"); 
                profEmail.setText(email != null ? email : "");

                if (picUrl != null && !picUrl.isEmpty() && !isDestroyed()) {
                    if (picUrl.startsWith("content://")) {
                        String migratedPath = copyProfilePicture(Uri.parse(picUrl));
                        if (migratedPath != null) {
                            displayProfilePicture(migratedPath);
                            db.child("users").child(auth.getUid()).child("photoUrl").setValue(migratedPath);
                        }
                    } else if (picUrl.startsWith("/") && !new File(picUrl).exists()) {
                        imgUserAvatar.setImageResource(R.drawable.ic_user);
                        imgProfileAvatar.setImageResource(R.drawable.ic_user);
                    } else {
                        displayProfilePicture(picUrl);
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError e) { userDataLoading = false; }
        });
    }

    private void displayProfilePicture(String pictureReference) {
        if (pictureReference == null || pictureReference.isEmpty() || isDestroyed()) return;
        Uri pictureUri = pictureReference.startsWith("/") ? Uri.fromFile(new File(pictureReference)) : Uri.parse(pictureReference);
        Glide.with(this).load(pictureUri).circleCrop().into(imgUserAvatar);
        Glide.with(this).load(pictureUri).circleCrop().into(imgProfileAvatar);
    }

    private String copyProfilePicture(Uri source) {
        if (auth.getUid() == null) return null;
        File target = new File(getFilesDir(), "profile_" + historyKey(auth.getUid()) + ".img");
        try (InputStream input = getContentResolver().openInputStream(source);
             FileOutputStream output = new FileOutputStream(target)) {
            if (input == null) return null;
            byte[] buffer = new byte[8192];
            int read;
            while ((read = input.read(buffer)) != -1) output.write(buffer, 0, read);
            return target.getAbsolutePath();
        } catch (Exception error) {
            return null;
        }
    }

    private void removeProfilePicture() {
        if (auth.getUid() == null) return;
        imgUserAvatar.setImageResource(R.drawable.ic_user);
        imgProfileAvatar.setImageResource(R.drawable.ic_user);
        File localPicture = new File(getFilesDir(), "profile_" + historyKey(auth.getUid()) + ".img");
        if (localPicture.exists()) localPicture.delete();
        db.child("users").child(auth.getUid()).child("photoUrl").setValue("")
                .addOnSuccessListener(ignored -> Toast.makeText(this, "Profile picture removed.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(error -> Toast.makeText(this, "Could not remove profile picture.", Toast.LENGTH_SHORT).show());
    }

    private void addCoins(int amount, String desc) {
        if (auth.getUid() == null) return;
        db.child("users").child(auth.getUid()).child("points").runTransaction(new Transaction.Handler() {
            @NonNull @Override public Transaction.Result doTransaction(MutableData currentData) {
                Integer currentPoints = currentData.getValue(Integer.class);
                currentData.setValue((currentPoints != null ? currentPoints : 0) + amount);
                return Transaction.success(currentData);
            }
            @Override public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                if (committed && snapshot != null) {
                    Integer updatedPoints = snapshot.getValue(Integer.class);
                    if (updatedPoints != null) {
                        userCoins = updatedPoints;
                        updateCoinViews();
                    }
                }
            }
        });
    }

    private void updateCoinViews() {
        if (tvMainCoins != null) tvMainCoins.setText(String.valueOf(userCoins));
        if (profCoins != null) profCoins.setText("🪙 " + userCoins);
    }

    private String todayKey() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }

    private void updateDailyRewardState(String claimedDate) {
        dailyRewardClaimed = todayKey().equals(claimedDate);
        String label = dailyRewardInProgress ? "CLAIMING..." : (dailyRewardClaimed ? "CLAIMED TODAY" : "CLAIM DAILY REWARD (+20)");
        String status = dailyRewardInProgress ? "Claiming..." : (dailyRewardFeedback != null ? dailyRewardFeedback : (dailyRewardClaimed ? "Daily reward already claimed today." : "Claim 20 coins once every day."));
        if (dailyRewardStatus != null) dailyRewardStatus.setText(status);
        if (dailyRewardClaim != null) {
            dailyRewardClaim.setText(label);
            dailyRewardClaim.setEnabled(!dailyRewardClaimed && !dailyRewardInProgress);
            dailyRewardClaim.setBackgroundResource(dailyRewardClaimed ? R.drawable.bg_card : R.drawable.bg_btn_gold);
        }
        Button profileClaim = findViewById(R.id.btnDailyClaim);
        profileClaim.setText(label);
        profileClaim.setEnabled(!dailyRewardClaimed && !dailyRewardInProgress);
    }

    private void claimDailyReward() {
        if (auth.getUid() == null || dailyRewardClaimed || dailyRewardInProgress) {
            Toast.makeText(this, "Daily reward already claimed today.", Toast.LENGTH_SHORT).show();
            return;
        }
        String today = todayKey();
        dailyRewardInProgress = true;
        dailyRewardFeedback = null;
        updateDailyRewardState(dailyRewardClaimed ? today : null);
        claimCoinsOnce("daily-" + today, QuizBank.DAILY_REWARD_COINS, "Daily Bonus", today,
            (committed, alreadyClaimed, error) -> {
                dailyRewardInProgress = false;
                if (committed) {
                    dailyRewardClaimed = true;
                    dailyRewardFeedback = "Reward claimed: +20 coins";
                    updateDailyRewardState(today);
                    Toast.makeText(this, "+" + QuizBank.DAILY_REWARD_COINS + " Daily Coins Added!", Toast.LENGTH_SHORT).show();
                } else if (alreadyClaimed) {
                    dailyRewardClaimed = true;
                    dailyRewardFeedback = "Daily reward already claimed today.";
                    updateDailyRewardState(today);
                    Toast.makeText(this, "Daily reward already claimed today.", Toast.LENGTH_SHORT).show();
                } else {
                    dailyRewardClaimed = false;
                    dailyRewardFeedback = "Reward failed - try again";
                    updateDailyRewardState(null);
                    Toast.makeText(this, "Reward failed - try again", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void claimAdReward() {
        Button rewardButton = findViewById(R.id.btnEarnMoreCoins);
        if (adRewardInProgress) return;
        if (mRewardedAd == null) {
            rewardButton.setText("AD UNAVAILABLE");
            Toast.makeText(this, "The test ad is still loading. Try again shortly.", Toast.LENGTH_SHORT).show();
            loadRewardedAd();
            rewardButton.postDelayed(() -> rewardButton.setText("WATCH AD +" + WATCH_AD_REWARD_COINS), 1500);
            return;
        }

        adRewardInProgress = true;
        rewardButton.setEnabled(false);
        rewardButton.setText("CLAIMING...");
        String claimId = "ad-" + UUID.randomUUID();
        final boolean[] rewardEarned = {false};
        RewardedAd ad = mRewardedAd;
        mRewardedAd = null;
        ad.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override public void onAdDismissedFullScreenContent() {
                loadRewardedAd();
                if (!rewardEarned[0]) finishAdReward(false, "Ad closed before the reward was earned.");
            }

            @Override public void onAdFailedToShowFullScreenContent(@NonNull AdError error) {
                finishAdReward(false, "The ad could not be shown. No coins were added.");
            }
        });
        ad.show(this, reward -> {
            rewardEarned[0] = true;
            claimCoinsOnce(claimId, WATCH_AD_REWARD_COINS, "Video Ad", null,
                (committed, alreadyClaimed, error) -> {
                    if (committed) finishAdReward(true, "+" + WATCH_AD_REWARD_COINS + " Coins Claimed!");
                    else if (alreadyClaimed) finishAdReward(false, "This ad reward was already claimed.");
                    else finishAdReward(false, "Could not claim the ad reward. Try again.");
                });
        });
    }

    private void finishAdReward(boolean success, String message) {
        adRewardInProgress = false;
        Button rewardButton = findViewById(R.id.btnEarnMoreCoins);
        rewardButton.setEnabled(true);
        rewardButton.setText("WATCH AD +" + WATCH_AD_REWARD_COINS);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void claimCoinsOnce(String claimId, int amount, String description, String dailyDate, RewardClaimCallback callback) {
        if (auth.getUid() == null) {
            callback.onComplete(false, false, null);
            return;
        }
        db.child("users").child(auth.getUid()).runTransaction(new Transaction.Handler() {
            @NonNull @Override public Transaction.Result doTransaction(MutableData currentData) {
                MutableData claim = currentData.child("rewardClaims").child(claimId);
                if (claim.getValue() != null) return Transaction.abort();
                Integer currentPoints = currentData.child("points").getValue(Integer.class);
                currentData.child("points").setValue((currentPoints != null ? currentPoints : 0) + amount);
                claim.child("amount").setValue(amount);
                claim.child("description").setValue(description);
                claim.child("claimedAt").setValue(ServerValue.TIMESTAMP);
                if (dailyDate != null) currentData.child("dailyRewardDate").setValue(dailyDate);
                return Transaction.success(currentData);
            }
            @Override public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                boolean alreadyClaimed = error == null && !committed && snapshot != null
                    && snapshot.child("rewardClaims").child(claimId).exists();
                if (committed && snapshot != null) {
                    Integer updatedPoints = snapshot.child("points").getValue(Integer.class);
                    if (updatedPoints != null) {
                        userCoins = updatedPoints;
                        updateCoinViews();
                    }
                }
                callback.onComplete(committed, alreadyClaimed, error);
            }
        });
    }

    private void loadTournaments() {
        tournamentsLoading = true;
        if (tournamentsProgress != null) tournamentsProgress.setVisibility(View.VISIBLE);
        db.child("tournaments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot s) {
                tournamentsLoading = false;
                if (tournamentsProgress != null) tournamentsProgress.setVisibility(View.GONE);
            tournamentsLoaded = true;
                tournamentList.clear();
                List<Tournament> upcoming = new ArrayList<>();
                List<Tournament> completed = new ArrayList<>();
                if (s.exists()) {
                    for (DataSnapshot ds : s.getChildren()) {
                        Tournament t = ds.getValue(Tournament.class);
                        if (t != null && isPublished(ds)) {
                            setTournamentStatus(t);
                            t.id = ds.getKey();
                            t.playedCount = (int) ds.child("players").getChildrenCount();
                            t.hasPlayed = auth.getUid() != null && ds.child("players").hasChild(auth.getUid());
                            if ("UPCOMING".equals(t.status)) upcoming.add(t);
                            else if ("ENDED".equals(t.status)) completed.add(t);
                            else tournamentList.add(t);
                        }
                    }
                }
                tournamentList.sort(Comparator.comparingLong(t -> t.endTime));
                upcoming.sort(Comparator.comparingLong(t -> t.startTime));
                completed.sort((left, right) -> Long.compare(right.endTime, left.endTime));
                if (tournamentsEmpty != null) tournamentsEmpty.setVisibility(tournamentList.isEmpty() ? View.VISIBLE : View.GONE);
                if (upcomingEmpty != null) upcomingEmpty.setVisibility(upcoming.isEmpty() ? View.VISIBLE : View.GONE);
                recyclerTournamentsFull.setAdapter(new TournamentFullAdapter(tournamentList));
                recyclerTournamentsUpcoming.setAdapter(new TournamentFullAdapter(upcoming));
                recyclerTournamentsCompleted.setAdapter(new TournamentFullAdapter(completed));
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {
                tournamentsLoading = false;
                if (tournamentsProgress != null) tournamentsProgress.setVisibility(View.GONE);
                if (tournamentsEmpty != null) { tournamentsEmpty.setText("Could not load tournaments: " + e.getMessage()); tournamentsEmpty.setVisibility(View.VISIBLE); }
            }
        });
    }

    private boolean isPublished(DataSnapshot snapshot) {
        Boolean published = snapshot.child("published").getValue(Boolean.class);
        return Boolean.TRUE.equals(published) || "true".equalsIgnoreCase(snapshot.child("published").getValue(String.class));
    }

    private void setTournamentStatus(Tournament tournament) {
        long now = System.currentTimeMillis();
        if (tournament.startTime > 0 && now < tournament.startTime) {
            tournament.status = "UPCOMING";
        } else if (tournament.endTime > 0 && now >= tournament.endTime) {
            tournament.status = "ENDED";
        } else {
            tournament.status = "ONGOING";
        }
    }

    private void loadQuickCategoriesFromFirebase() {
        if (auth.getUid() == null) return;
        quickCategoriesLoading = true;
        if (categoriesProgress != null) categoriesProgress.setVisibility(View.VISIBLE);
        db.child("quick_categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snap) {
                quickCategoriesLoading = false;
                if (categoriesProgress != null) categoriesProgress.setVisibility(View.GONE);
                quickCategoriesLoaded = true;
                quickCategoryList.clear();
                List<QuizBank.StarterCategory> starterCategories = QuizBank.categories();
                Map<String, DataSnapshot> firebaseCategories = new HashMap<>();
                if (snap.exists()) {
                    for (DataSnapshot dSnap : snap.getChildren()) {
                        String title = dSnap.child("title").getValue(String.class);
                        if (findStarterCategory(starterCategories, title) != null && !firebaseCategories.containsKey(title)) {
                            firebaseCategories.put(title, dSnap);
                        }
                    }
                }
                for (QuizBank.StarterCategory starter : starterCategories) {
                    DataSnapshot firebaseCategory = firebaseCategories.get(starter.title);
                    List<QuizQuestion> questions = firebaseCategory == null
                            ? starterQuestions(starter)
                            : firebaseQuestions(firebaseCategory);
                        if (questions.size() > 20) questions = new ArrayList<>(questions.subList(0, 20));
                    List<String[]> missingQuestions = new ArrayList<>();
                    Set<String> existingQuestionTexts = new HashSet<>();
                    for (QuizQuestion question : questions) existingQuestionTexts.add(question.q);
                    for (int index = 0; index < starter.questions.length && questions.size() < 20; index++) {
                        String[] item = starter.questions[index];
                        if (existingQuestionTexts.add(item[0])) {
                            questions.add(new QuizQuestion(starterKey(starter.title) + "-q" + index,
                                    item[0], item[1], item[2], item[3], item[4], Integer.parseInt(item[5]), 10));
                            missingQuestions.add(item);
                        }
                    }
                    if (firebaseCategory == null) {
                        seedStarterCategories(Collections.singletonList(starter));
                    } else if (!missingQuestions.isEmpty()) {
                        seedQuestions(firebaseCategory.getKey(), missingQuestions);
                    }
                    String categoryId = firebaseCategory == null ? starterKey(starter.title) : firebaseCategory.getKey();
                    String icon = firebaseCategory == null ? starter.icon : firebaseCategory.child("icon").getValue(String.class);
                    quickCategoryList.add(new QuickCategory(categoryId, starter.title,
                            icon == null || icon.isEmpty() ? starter.icon : icon, questions));
                }
                recyclerCategories.setAdapter(new QuickCategoryAdapter(quickCategoryList));
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {
                quickCategoriesLoading = false;
                if (categoriesProgress != null) categoriesProgress.setVisibility(View.GONE);
            }
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

    private List<QuizQuestion> firebaseQuestions(DataSnapshot category) {
        List<QuizQuestion> questions = new ArrayList<>();
        DataSnapshot questionSnapshot = category.child("questions");
        for (DataSnapshot question : questionSnapshot.getChildren()) {
            String text = question.child("q").getValue(String.class);
            String optionA = question.child("opt1").getValue(String.class);
            String optionB = question.child("opt2").getValue(String.class);
            String optionC = question.child("opt3").getValue(String.class);
            String optionD = question.child("opt4").getValue(String.class);
            Integer answer = question.child("ansIdx").getValue(Integer.class);
            if (answer == null) answer = answerIndex(question.child("correctAnswer").getValue(String.class));
            answer = Math.max(0, Math.min(3, answer));
            if (text != null && optionA != null && optionB != null && optionC != null && optionD != null) {
                questions.add(new QuizQuestion(question.getKey(), text, optionA, optionB, optionC, optionD,
                        answer, 10));
            }
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

    private void seedQuestions(String categoryKey, List<String[]> questions) {
        if (categoryKey == null) return;
        for (String[] item : questions) db.child("quick_categories").child(categoryKey).child("questions").push().setValue(starterQuestionData(item));
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
        leaderboardLoading = true;
        if (leaderboardProgress != null) leaderboardProgress.setVisibility(View.VISIBLE);
        db.child("users").orderByChild("points").limitToLast(50).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot s) {
                leaderboardLoading = false;
                if (leaderboardProgress != null) leaderboardProgress.setVisibility(View.GONE);
            leaderboardLoaded = true;
                leaderboardList.clear();
                for (DataSnapshot ds : s.getChildren()) { User u = ds.getValue(User.class); if (u != null) leaderboardList.add(0, u); }
                recyclerLeaderboardFull.setAdapter(new LeaderboardAdapter(leaderboardList));
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {
                leaderboardLoading = false;
                if (leaderboardProgress != null) leaderboardProgress.setVisibility(View.GONE);
            }
        });
    }

    private void startQuiz(Tournament t) {
        if (t.hasPlayed) { Toast.makeText(this, "You already completed this pool!", Toast.LENGTH_SHORT).show(); return; }
        if (!"ONGOING".equals(t.status)) { Toast.makeText(this, "This tournament is not ongoing.", Toast.LENGTH_SHORT).show(); return; }

        activeTournament = t; activeQuickCategory = null;
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
        answerLocked = false;
        quizActive = true;
        quizAttemptId = UUID.randomUUID().toString();
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
        if (!quizActive) return;
        if (currentQIndex >= currentQuizQuestions.size()) { completeQuiz(); return; }
        if (countDownTimer != null) countDownTimer.cancel();
        for (Button b : btnOpts) {
            b.setBackgroundResource(R.drawable.bg_option_btn);
            b.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            b.setEnabled(true);
        }
        answerLocked = false;
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
        if (!quizActive || answerLocked || currentQIndex >= currentQuizQuestions.size()) return;
        answerLocked = true;
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
            if (activeTournament == null) coinsEarnedInQuiz += 10;
            quizPointsCurrent.setText("+10 coins");
        } else {
            if (idx >= 0) setOptionColor(btnOpts[idx], R.color.wrong_answer, R.color.wrong_answer, R.color.text_primary);
            setOptionColor(btnOpts[q.ansIdx], R.color.correct_answer, R.color.correct_answer, R.color.text_primary);
            quizPointsCurrent.setText("10 coins deducted");
        }
        applyAnswerCoinDelta(q, idx == q.ansIdx);
    }

    private void applyAnswerCoinDelta(QuizQuestion question, boolean correct) {
        if (auth.getUid() == null || quizAttemptId == null || question.id == null) {
            advanceAfterAnswer();
            return;
        }
        String answerKey = historyKey(quizAttemptId + "-" + question.id);
        db.child("users").child(auth.getUid()).runTransaction(new Transaction.Handler() {
            @NonNull @Override public Transaction.Result doTransaction(MutableData currentData) {
                MutableData answer = currentData.child("quizAnswers").child(answerKey);
                if (answer.getValue() != null) return Transaction.abort();
                Integer currentPoints = currentData.child("points").getValue(Integer.class);
                int balance = currentPoints == null ? 0 : Math.max(0, currentPoints);
                int updatedBalance = correct ? balance + 10 : Math.max(0, balance - 10);
                currentData.child("points").setValue(updatedBalance);
                answer.child("correct").setValue(correct);
                answer.child("delta").setValue(correct ? 10 : -10);
                answer.child("recordedAt").setValue(ServerValue.TIMESTAMP);
                return Transaction.success(currentData);
            }
            @Override public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                if (committed && snapshot != null) {
                    Integer updated = snapshot.child("points").getValue(Integer.class);
                    if (updated != null) { userCoins = Math.max(0, updated); updateCoinViews(); }
                }
                if (error != null) quizPointsCurrent.setText("Balance update failed");
                advanceAfterAnswer();
            }
        });
    }

    private void advanceAfterAnswer() {
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

    private void exitQuiz() {
        if (!quizActive) return;
        if (countDownTimer != null) countDownTimer.cancel();
        quizActive = false;
        quizContainer.setVisibility(View.GONE);
        currentQuizQuestions.clear();
    }

    private void completeQuiz() {
        if (!quizActive) return;
        quizActive = false;
        if (countDownTimer != null) countDownTimer.cancel();
        quizContainer.setVisibility(View.GONE);
        
        // --- Show Interstitial Ad When Quiz Ends ---
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
            loadInterstitialAd(); // Load the next one in the background
        }
        
        int totalQ = currentQuizQuestions.size();
        if (totalQ <= 0) return;
        recordQuizResult(totalQ);
        if (activeTournament != null) {
            showResultDialog("Tournament Complete", "You scored " + correctAnswersCount + "/" + totalQ + ".\nYour result was recorded for the free leaderboard.", "🏆");
        } else {
            showResultDialog("Quiz Complete", "You scored " + correctAnswersCount + "/" + totalQ + ".\nEach correct answer earned 10 coins and each wrong answer deducted up to 10 coins.", "🎉");
        }
    }

    private void recordQuizResult(int totalQuestions) {
        if (auth.getCurrentUser() == null || totalQuestions <= 0 || quizAttemptId == null) return;
        Map<String, Object> result = new HashMap<>();
        int wrongAnswers = totalQuestions - correctAnswersCount;
        double percentage = (correctAnswersCount * 100.0) / totalQuestions;
        result.put("attemptId", quizAttemptId);
        result.put("userId", auth.getUid());
        result.put("userName", userNameStr);
        result.put("category", activeQuickCategory != null ? activeQuickCategory.title : (activeTournament != null ? activeTournament.title : "Quiz"));
        result.put("tournamentId", activeTournament == null ? "" : activeTournament.id);
        result.put("score", coinsEarnedInQuiz);
        result.put("correctAnswers", correctAnswersCount);
        result.put("wrongAnswers", wrongAnswers);
        result.put("totalQuestions", totalQuestions);
        result.put("percentage", percentage);
        result.put("timestamp", ServerValue.TIMESTAMP);
        result.put("completedAt", ServerValue.TIMESTAMP);
        result.put("published", false);
        String resultPath = activeTournament == null ? "quiz_results" : "tournament_attempts";
        db.child(resultPath).child(historyKey(quizAttemptId)).setValue(result);
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
            h.tDetails.setText(t.status + " | FREE | " + t.category + " | " + t.totalQuestions + " questions\n" + formatTournamentTime(t.startTime) + " - " + formatTournamentTime(t.endTime));
            
            // 3D Realistic Golden Cup pulse animation
            Animation pulse = AnimationUtils.loadAnimation(h.itemView.getContext(), R.anim.pulse_anim);
            h.tCupImage.startAnimation(pulse);

            if ("ENDED".equals(t.status)) {
                h.btnPlay.setText("COMPLETED"); h.btnPlay.setEnabled(false); h.btnPlay.setBackgroundResource(R.drawable.bg_card); h.btnPlay.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.text_secondary));
            } else if ("UPCOMING".equals(t.status)) {
                h.btnPlay.setText("UPCOMING"); h.btnPlay.setEnabled(false); h.btnPlay.setBackgroundResource(R.drawable.bg_card); h.btnPlay.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.text_secondary));
            } else if (t.hasPlayed) {
                h.btnPlay.setText("PLAYED"); h.btnPlay.setEnabled(false); h.btnPlay.setBackgroundColor(0xFF3B2A5E); h.btnPlay.setTextColor(0xFF7A6B99);
            } else {
                h.btnPlay.setText("JOIN"); h.btnPlay.setEnabled(true); h.btnPlay.setBackgroundResource(R.drawable.bg_btn_gold); h.btnPlay.setTextColor(0xFF2A1B00);
            }
            h.btnPlay.setOnClickListener(v -> startQuiz(t));
        }
        private String formatTournamentTime(long time) {
            if (time <= 0) return "Time unavailable";
            return new java.text.SimpleDateFormat("MMM d, h:mm a", Locale.US).format(new Date(time));
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
    public static class Tournament { public String id, title, description, category, status, icon, createdBy; public int totalQuestions, entry_points, reward_points, pool_size, playedCount; public long startTime, endTime, createdAt; public boolean published, hasPlayed; public Tournament(){} }
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
