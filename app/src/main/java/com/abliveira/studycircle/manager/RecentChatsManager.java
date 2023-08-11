package com.abliveira.studycircle.manager;

import java.util.ArrayList;
import java.util.List;

public class RecentChatsManager {
    private static RecentChatsManager instance;
    private List<String> chatPreviews;
    private static final int MAX_PREVIEW_LENGTH = 120;
    private static final int MAX_PREVIEWS = 3;

    private RecentChatsManager() {
        chatPreviews = new ArrayList<>();
    }

    public static synchronized RecentChatsManager getInstance() {
        if (instance == null) {
            instance = new RecentChatsManager();
        }
        return instance;
    }

    public void addChatPreview(String preview) {
        if (chatPreviews.size() >= MAX_PREVIEWS) {
            chatPreviews.remove(0);
        }
        chatPreviews.add(preview);
    }

    public void setChatPreview(int index, String preview) {
        if (index >= 0) {
            while (index >= chatPreviews.size()) {
                chatPreviews.add(null);
            }
            chatPreviews.set(index, truncateString(preview));
        } else {
            System.out.println("Invalid index.");
        }
    }

    public String getChatPreview(int index) {
        if (index >= 0 && index < chatPreviews.size()) {
            return chatPreviews.get(index);
        }
        return null;
    }

    public List<String> getChatPreviews() {
        return chatPreviews;
    }

    private String truncateString(String input) {
        if (input.length() > MAX_PREVIEW_LENGTH) {
            return input.substring(0, MAX_PREVIEW_LENGTH) + "...";
        }
        return input;
    }
}