package dev.hassanabid.ioextendedsg.algo

import java.util.LinkedList

class Solution {

    fun reverseWords(s: String): String {
        var i = s.lastIndex
        val str = StringBuilder()
        while (i >= 0) {
            if (s[i] == ' ') {
                // skip spaces
                i--
            } else {
                // search for the beginning and end of a word
                val end = i
                while (i >= 0 && s[i] != ' ') {
                    i--
                }
                // add space between words
                if (str.isNotEmpty()) {
                    str.append(' ')
                }
                // add the word
                for (j in i + 1..end) {
                    str.append(s[j])
                }
            }
        }
        return str.toString()
    }

    fun reverseVowels(s: String): String {
        val vowels = setOf('a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U')
        var chrs = s.toCharArray()
        var l = 0
        var r = chrs.lastIndex
        while(l < r) {
            while(l<r && chrs[l] !in vowels) l++
            while(l<r && chrs[r] !in vowels) r--
            if (l < r) chrs[l] = chrs[r].also { chrs[r] = chrs[l] }
            r--
            l++
        }
        return String(chrs)
    }


}