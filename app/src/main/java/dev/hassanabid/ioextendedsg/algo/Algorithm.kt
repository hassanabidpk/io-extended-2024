package dev.hassanabid.ioextendedsg.algo

import java.util.LinkedList

class Solution {
    fun removeElement(nums: IntArray, `val`: Int): Int {
        val list = IntArray(nums.size)
        var j = 0

        for (k in 0 until nums.size){
            list[k] = nums[k]
            nums[k] = 0
        }

        for (i in 0 until nums.size) {
            if (list[i] != `val`) {
                nums[j] = list[i]
                j++
            }
        }
        println(nums.contentToString())
        return j
    }
}