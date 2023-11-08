package site.kotty_kov.powertodo.todolist.main.notepad;

import java.util.HashMap;

public class UndoRedoStore {
    private HashMap<String, Object> myHashMap = new HashMap<String, Object>();

    @Override
    public String toString() {
        return "UndoRedoStore{" +
                "myHashMap=" + myHashMap +
                '}';
    }

    public void putString(String key, String value) {
        myHashMap.put(key, value);
    }

    public void putInt(String key, int value) {
        myHashMap.put(key, value);
    }

    public String getString(String key, String back) {
        String a = "";
        try {
            a = (String) myHashMap.get(key);
        } catch (ClassCastException e) {
            return back;
        }
        if (a == null) {
            return back;
        } else {
            return a;
        }
    }

    public int getInt(String key, int back) {
        Integer a = 0;
        try {
            a = (Integer) myHashMap.get(key);
        } catch (ClassCastException e) {
            try {
                a = (int) Math.round((Double) myHashMap.get(key));
            } catch (ClassCastException e1) {
                return back;
            }
        }
        if (a == null) {
            return back;
        } else {
            return a;
        }
    }
}