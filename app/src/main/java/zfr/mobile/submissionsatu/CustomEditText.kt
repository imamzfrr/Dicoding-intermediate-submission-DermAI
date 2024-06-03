package zfr.mobile.submissionsatu

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText


class CustomEditText : AppCompatEditText {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateInput(s.toString())
            }
        })
    }

    private fun validateInput(input: String) {
        if (this.id == R.id.emailEditText) {
            if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                this.error = "Format email salah"
            } else {
                this.error = null
            }
        } else if (this.id == R.id.passwordEditText) {
            if (input.length < 8) {
                this.error = "Password harus memiliki minimal 8 karakter"
            } else {
                this.error = null
            }
        }
    }
}