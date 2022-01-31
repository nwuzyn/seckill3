class Solution {
    public String reverseWords(String s) {
        StringBuilder sb = new StringBuilder();
        s = s.trim();
        int slow = s.length() - 1, fast = s.length() - 1;
        while(fast >= 0) {
            if((s.charAt(fast)) == ' ') {
                sb.append(s.substring(fast + 1, slow + 1));
                fast--;
                slow = fast;
            } else if(fast == 0) {
                sb.append(s.substring(fast, slow + 1));
                sb.append(" ");
                fast--;
            } else {
                fast--;
            }
        }
        return sb.toString().trim();
    }

    public static void main(String[] args) {
        Solution solution = new Solution();
        solution.reverseWords("the sky is blue");
    }
}