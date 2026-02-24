package tn.esprit.services;

import tn.esprit.entities.DailyRoutineTask;
import tn.esprit.entities.WellbeingTracker;
import tn.esprit.entities.User;
import java.util.*;
import java.util.stream.Collectors;

public class Search {

    private List<DailyRoutineTask> tasks = new ArrayList<>();
    private List<WellbeingTracker> trackers = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private final Map<String, List<SearchResult>> searchIndex = new HashMap<>();
    private final List<String> recentSearches = new ArrayList<>();

    public static class SearchResult {
        public Object entity;
        public String entityType;
        public String title;
        public String subtitle;
        public String date;
        public int id;
        public int userId;
        public String userName;
        public double relevance;
        public List<DailyRoutineTask> userTasks = new ArrayList<>();
        public List<WellbeingTracker> userWellbeing = new ArrayList<>();

        // Constructor for User result (this will group their tasks and wellbeing)
        public SearchResult(User user, List<DailyRoutineTask> userTasks, List<WellbeingTracker> userWellbeing) {
            this.entity = user;
            this.entityType = "USER";
            this.title = user.getNom();
            this.subtitle = "📧 " + user.getEmail() + " | 📞 " + (user.getTelephone() != null ? user.getTelephone() : "N/A");
            this.date = "";
            this.id = user.getId();
            this.userId = user.getId();
            this.userName = user.getNom();
            this.userTasks = userTasks;
            this.userWellbeing = userWellbeing;
        }

        // Constructor for Task result
        public SearchResult(DailyRoutineTask task, String userName) {
            this.entity = task;
            this.entityType = "TASK";
            this.title = task.getTitle();
            this.subtitle = "👤 " + userName + " (ID: " + task.getUserId() + ")";
            this.date = task.getCreatedAt();
            this.id = task.getId();
            this.userId = task.getUserId();
            this.userName = userName;
        }

        // Constructor for Wellbeing result
        public SearchResult(WellbeingTracker tracker, String userName) {
            this.entity = tracker;
            this.entityType = "WELLBEING";
            this.title = "😊 Mood: " + tracker.getMood() + "/5 | 😴 Sleep: " + tracker.getSleepHours() + "h";
            this.subtitle = "👤 " + userName + " | 📅 " + tracker.getDate();
            this.date = tracker.getDate();
            this.id = tracker.getId();
            this.userId = tracker.getUserId();
            this.userName = userName;
        }

        public String getDisplayText() {
            switch (entityType) {
                case "USER":
                    return "👥 " + title + " - " + subtitle + " | Tasks: " + userTasks.size() + ", Wellbeing: " + userWellbeing.size();
                case "TASK":
                    return "✅ " + title + " - " + subtitle;
                case "WELLBEING":
                    return "🌿 " + title + " - " + subtitle;
                default:
                    return "";
            }
        }

        public String getIcon() {
            switch (entityType) {
                case "USER": return "👥";
                case "TASK": return "✅";
                case "WELLBEING": return "🌿";
                default: return "📌";
            }
        }

        public String getColorStyle() {
            switch (entityType) {
                case "USER": return "-fx-text-fill: #2980B9; -fx-font-weight: bold;";
                case "TASK": return "-fx-text-fill: #3A7D6B;";
                case "WELLBEING": return "-fx-text-fill: #9B59B6;";
                default: return "-fx-text-fill: #2C3E50;";
            }
        }

        public String getStatsText() {
            if (entityType.equals("USER")) {
                return "📊 " + userTasks.size() + " tâches | " + userWellbeing.size() + " suivis";
            }
            return "";
        }
    }

    public enum SearchFilter {
        ALL, USERS, TASKS, WELLBEING, RECENT
    }

    public void setData(List<DailyRoutineTask> tasks, List<WellbeingTracker> trackers, List<User> users) {
        this.tasks = tasks != null ? tasks : new ArrayList<>();
        this.trackers = trackers != null ? trackers : new ArrayList<>();
        this.users = users != null ? users : new ArrayList<>();
        buildSearchIndex();
    }

    private void buildSearchIndex() {
        searchIndex.clear();

        // Create a map for quick user lookup
        Map<Integer, String> userNames = new HashMap<>();
        Map<Integer, User> userMap = new HashMap<>();
        for (User user : users) {
            userNames.put(user.getId(), user.getNom());
            userMap.put(user.getId(), user);

            // Index users by name
            SearchResult userResult = new SearchResult(user,
                    getTasksForUser(user.getId()),
                    getWellbeingForUser(user.getId()));

            // Index user's full name and parts
            String fullName = user.getNom().toLowerCase();
            indexText(fullName, userResult);

            // Index first name and last name separately (split by space)
            String[] nameParts = fullName.split("\\s+");
            for (String part : nameParts) {
                if (part.length() > 1) {
                    indexText(part, userResult);
                }
            }

            // Index email
            if (user.getEmail() != null) {
                indexText(user.getEmail().toLowerCase(), userResult);
            }

            // Index phone
            if (user.getTelephone() != null) {
                indexText(user.getTelephone(), userResult);
            }
        }

        // Index tasks with user names
        for (DailyRoutineTask task : tasks) {
            String userName = userNames.getOrDefault(task.getUserId(), "Unknown User");
            SearchResult result = new SearchResult(task, userName);

            indexText(task.getTitle().toLowerCase(), result);
            indexText(String.valueOf(task.getUserId()), result);
            indexText(userName.toLowerCase(), result); // Index by user name
            indexText(task.getCreatedAt(), result);
            indexText(task.isCompleted() ? "complété" : "en cours", result);
            indexText("tâche", result);
            indexText("task", result);
        }

        // Index wellbeing trackers with user names
        for (WellbeingTracker tracker : trackers) {
            String userName = userNames.getOrDefault(tracker.getUserId(), "Unknown User");
            SearchResult result = new SearchResult(tracker, userName);

            indexText("mood " + tracker.getMood(), result);
            indexText("stress " + tracker.getStress(), result);
            indexText("energy " + tracker.getEnergy(), result);
            indexText("sommeil " + tracker.getSleepHours(), result);
            indexText("sommeil " + (int)tracker.getSleepHours() + "h", result);
            indexText(tracker.getDate(), result);
            indexText(String.valueOf(tracker.getUserId()), result);
            indexText(userName.toLowerCase(), result); // Index by user name
            indexText("bien-être", result);
            indexText("wellbeing", result);
            indexText("tracker", result);
            if (tracker.getNote() != null && !tracker.getNote().isEmpty()) {
                indexText(tracker.getNote().toLowerCase(), result);
            }
        }
    }

    private List<DailyRoutineTask> getTasksForUser(int userId) {
        return tasks.stream()
                .filter(t -> t.getUserId() == userId)
                .collect(Collectors.toList());
    }

    private List<WellbeingTracker> getWellbeingForUser(int userId) {
        return trackers.stream()
                .filter(t -> t.getUserId() == userId)
                .collect(Collectors.toList());
    }

    private void indexText(String text, SearchResult result) {
        if (text == null || text.isEmpty()) return;

        String[] words = text.split("\\s+");
        for (String word : words) {
            if (word.length() > 1) {
                for (int i = 1; i <= word.length(); i++) {
                    String prefix = word.substring(0, i);
                    searchIndex.computeIfAbsent(prefix, k -> new ArrayList<>());
                    if (!searchIndex.get(prefix).contains(result)) {
                        searchIndex.get(prefix).add(result);
                    }
                }
            }
        }
    }

    public List<SearchResult> search(String query, SearchFilter filter) {
        if (query == null || query.trim().isEmpty()) {
            return getAllResults(filter);
        }

        String lowerQuery = query.toLowerCase().trim();
        Set<SearchResult> uniqueResults = new HashSet<>();

        // Add to recent searches
        addToRecentSearches(query);

        // Search in index
        for (Map.Entry<String, List<SearchResult>> entry : searchIndex.entrySet()) {
            if (entry.getKey().contains(lowerQuery)) {
                uniqueResults.addAll(entry.getValue());
            }
        }

        // If no results from index, do direct search
        if (uniqueResults.isEmpty()) {
            // Search users directly
            for (User user : users) {
                if (user.getNom().toLowerCase().contains(lowerQuery) ||
                        (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerQuery)) ||
                        (user.getTelephone() != null && user.getTelephone().contains(lowerQuery))) {
                    uniqueResults.add(new SearchResult(user,
                            getTasksForUser(user.getId()),
                            getWellbeingForUser(user.getId())));
                }
            }

            // Search tasks
            Map<Integer, String> userNames = getUserNameMap();
            for (DailyRoutineTask task : tasks) {
                String userName = userNames.getOrDefault(task.getUserId(), "Unknown");
                if (task.getTitle().toLowerCase().contains(lowerQuery) ||
                        String.valueOf(task.getUserId()).contains(lowerQuery) ||
                        userName.toLowerCase().contains(lowerQuery)) {
                    uniqueResults.add(new SearchResult(task, userName));
                }
            }

            // Search wellbeing
            for (WellbeingTracker tracker : trackers) {
                String userName = userNames.getOrDefault(tracker.getUserId(), "Unknown");
                if (String.valueOf(tracker.getMood()).contains(lowerQuery) ||
                        String.valueOf(tracker.getStress()).contains(lowerQuery) ||
                        String.valueOf(tracker.getEnergy()).contains(lowerQuery) ||
                        String.valueOf(tracker.getSleepHours()).contains(lowerQuery) ||
                        tracker.getDate().contains(lowerQuery) ||
                        userName.toLowerCase().contains(lowerQuery)) {
                    uniqueResults.add(new SearchResult(tracker, userName));
                }
            }
        }

        // Apply filter
        List<SearchResult> results = new ArrayList<>(uniqueResults);
        results = applyFilter(results, filter);

        // Sort by relevance
        for (SearchResult result : results) {
            result.relevance = calculateRelevance(result, lowerQuery);
        }
        results.sort((a, b) -> Double.compare(b.relevance, a.relevance));

        return results;
    }

    private Map<Integer, String> getUserNameMap() {
        Map<Integer, String> map = new HashMap<>();
        for (User user : users) {
            map.put(user.getId(), user.getNom());
        }
        return map;
    }

    public List<String> getSuggestions(String query, SearchFilter filter) {
        if (query == null || query.trim().isEmpty()) {
            return getRecentSearches();
        }

        String lowerQuery = query.toLowerCase().trim();
        Set<String> suggestions = new HashSet<>();

        // Add user name suggestions
        for (User user : users) {
            String fullName = user.getNom().toLowerCase();
            if (fullName.contains(lowerQuery)) {
                suggestions.add(user.getNom());
            }

            // Add first name suggestions
            String[] nameParts = fullName.split("\\s+");
            for (String part : nameParts) {
                if (part.startsWith(lowerQuery) && !part.equals(lowerQuery)) {
                    suggestions.add(part);
                }
            }
        }

        // Suggestions from index
        for (String key : searchIndex.keySet()) {
            if (key.startsWith(lowerQuery) && !key.equals(lowerQuery)) {
                suggestions.add(key);
                if (suggestions.size() >= 15) break;
            }
        }

        return new ArrayList<>(suggestions).stream()
                .limit(15)
                .collect(Collectors.toList());
    }

    private List<SearchResult> getAllResults(SearchFilter filter) {
        List<SearchResult> allResults = new ArrayList<>();
        Map<Integer, String> userNames = getUserNameMap();

        if (filter == SearchFilter.ALL || filter == SearchFilter.USERS) {
            for (User user : users) {
                allResults.add(new SearchResult(user,
                        getTasksForUser(user.getId()),
                        getWellbeingForUser(user.getId())));
            }
        }

        if (filter == SearchFilter.ALL || filter == SearchFilter.TASKS) {
            for (DailyRoutineTask task : tasks) {
                allResults.add(new SearchResult(task,
                        userNames.getOrDefault(task.getUserId(), "Unknown")));
            }
        }

        if (filter == SearchFilter.ALL || filter == SearchFilter.WELLBEING) {
            for (WellbeingTracker tracker : trackers) {
                allResults.add(new SearchResult(tracker,
                        userNames.getOrDefault(tracker.getUserId(), "Unknown")));
            }
        }

        return allResults;
    }

    private List<SearchResult> applyFilter(List<SearchResult> results, SearchFilter filter) {
        if (filter == SearchFilter.ALL) {
            return results;
        }

        return results.stream()
                .filter(r -> {
                    if (filter == SearchFilter.USERS) return r.entityType.equals("USER");
                    if (filter == SearchFilter.TASKS) return r.entityType.equals("TASK");
                    if (filter == SearchFilter.WELLBEING) return r.entityType.equals("WELLBEING");
                    return true;
                })
                .collect(Collectors.toList());
    }

    private double calculateRelevance(SearchResult result, String query) {
        double relevance = 0.0;
        String lowerQuery = query.toLowerCase();

        if (result.entityType.equals("USER")) {
            User user = (User) result.entity;
            if (user.getNom().toLowerCase().contains(lowerQuery)) {
                relevance += 20;
                if (user.getNom().toLowerCase().startsWith(lowerQuery)) {
                    relevance += 10;
                }
            }
            // More results = higher relevance
            relevance += result.userTasks.size() * 2;
            relevance += result.userWellbeing.size() * 2;
        }
        else if (result.entityType.equals("TASK")) {
            DailyRoutineTask task = (DailyRoutineTask) result.entity;
            if (task.getTitle().toLowerCase().contains(lowerQuery)) {
                relevance += 10;
            }
            if (result.userName.toLowerCase().contains(lowerQuery)) {
                relevance += 5;
            }
        }
        else if (result.entityType.equals("WELLBEING")) {
            if (result.userName.toLowerCase().contains(lowerQuery)) {
                relevance += 5;
            }
        }

        return relevance;
    }

    private void addToRecentSearches(String query) {
        recentSearches.remove(query);
        recentSearches.add(0, query);
        if (recentSearches.size() > 10) {
            recentSearches.remove(recentSearches.size() - 1);
        }
    }

    public List<String> getRecentSearches() {
        return new ArrayList<>(recentSearches);
    }

    public SearchResult getUserWithDetails(int userId) {
        for (User user : users) {
            if (user.getId() == userId) {
                return new SearchResult(user,
                        getTasksForUser(userId),
                        getWellbeingForUser(userId));
            }
        }
        return null;
    }

    public Map<String, Object> getSearchStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_users", users.size());
        stats.put("total_tasks", tasks.size());
        stats.put("total_wellbeing", trackers.size());
        stats.put("total_searches", recentSearches.size());
        stats.put("index_size", searchIndex.size());

        // Find user with most tasks
        Optional<Map.Entry<Integer, Long>> topUser = tasks.stream()
                .collect(Collectors.groupingBy(DailyRoutineTask::getUserId, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue());

        if (topUser.isPresent()) {
            int userId = topUser.get().getKey();
            getUserWithDetails(userId);
        }

        return stats;
    }
}