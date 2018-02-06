package com.tomi.savefacebook

import android.content.Intent
import android.os.Bundle
import com.github.paolorotolo.appintro.AppIntro
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import com.github.paolorotolo.appintro.AppIntroFragment


class Tutors : AppIntro() {
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val colores =  ContextCompat.getColor(this@Tutors,R.color.colorInsta)
        addSlide(AppIntroFragment.newInstance(getString(R.string.title_1),getString(R.string.step1), R.drawable.a1, colores ));
        addSlide(AppIntroFragment.newInstance(getString(R.string.title_2),getString(R.string.step2), R.drawable.a2, colores ));
        addSlide(AppIntroFragment.newInstance(getString(R.string.title_3),getString(R.string.step3), R.drawable.a3, colores ));





        askForPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA),3)




        // Hide Skip/Done button.
        showSkipButton(true)
        setSwipeLock(false)
        isProgressButtonEnabled = true


    }

    override fun onSkipPressed(currentFragment: Fragment) {
        super.onSkipPressed(currentFragment)
        startActivity(Intent(this@Tutors, MainActivity::class.java))
    }

    override fun onDonePressed(currentFragment: Fragment) {
        super.onDonePressed(currentFragment)
        startActivity(Intent(this@Tutors,MainActivity::class.java))

    }


}