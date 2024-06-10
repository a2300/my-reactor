package com.example;

public class LongestPalindrome {

    private String getPalindrome(String s, int index) {
        String result = "";
        int i = 0;
        while((index - i) >= 0 && (index + i) < s.length() ) {
            if(s.charAt(index - i) == s.charAt(index + i)) {
                result = s.substring(index - i, index+ i + 1);
            }else if(s.charAt(index) == s.charAt(index+i)) {
                result = s.substring(index, index+ i + 1);
            }
            i++;

        }
        return result;
    }
    public String longestPalindrome(String s) {
        String palindrome, longest = "";
        for (int i = 0; i < s.length(); i++) {
            palindrome = getPalindrome(s, i);
            if(palindrome.length() > longest.length()) {
                longest = palindrome;
            }
        }
        return longest;
    }

    public static void main(String[] args) {
        System.out.println(new LongestPalindrome().longestPalindrome("babad"));
        System.out.println(new LongestPalindrome().longestPalindrome("cbbd"));
    }
}
