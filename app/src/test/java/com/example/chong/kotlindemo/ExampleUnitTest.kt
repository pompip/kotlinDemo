package com.example.chong.kotlindemo

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val a1 = A()
        val a2 = a1;
        println(a2.b)
        a1.initA("1");
        println(a2.b)

    }

    class A {
        val b :String
            get() {
                return a;
            }

        var a = "c";
        fun initA(a: String) {
            this.a = a;

        }
        fun printa(){
            println(a)
        }


    }

}
