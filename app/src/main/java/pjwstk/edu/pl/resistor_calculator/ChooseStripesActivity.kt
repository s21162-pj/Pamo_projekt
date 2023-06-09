package pjwstk.edu.pl.resistor_calculator

import android.graphics.Color
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import java.io.*

class ChooseStripesActivity : AppCompatActivity() {
    private lateinit var stripeNumberSpinner: Spinner
    lateinit var stripeSpinnersContainer: LinearLayout
    lateinit var submitButton: Button
    private lateinit var saveButton: Button
    private lateinit var loadButton: Button
    private lateinit var deleteButton: Button
    private lateinit var configurationsSpinner: Spinner
    private lateinit var configurationNameEditText: EditText

    // Color options for different stripes
    val normalColorOptions = arrayOf(
        "None", "Black", "Brown", "Red", "Orange", "Yellow", "Green", "Blue", "Purple", "Gray", "White"
    )

    val multiplierColorOptions = arrayOf(
        "None", "Black", "Brown", "Red", "Orange", "Yellow", "Green", "Blue", "Purple", "Gray", "White", "Silver", "Gold"
    )

    val toleranceColorOptions = arrayOf(
        "None", "Brown", "Red", "Orange", "Yellow", "Green", "Blue", "Purple", "Gray", "Silver", "Gold"
    )

    val temperatureColorOptions = arrayOf(
        "None", "Black", "Brown", "Red", "Orange", "Yellow", "Green", "Blue", "Purple", "Gray"
    )

    var savedConfigurations = mutableListOf<Pair<String, List<Int>>>()

    @SuppressLint("MissingInflatedId")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_stripes)

        stripeNumberSpinner = findViewById(R.id.stripeNumberSpinner)
        stripeSpinnersContainer = findViewById(R.id.stripeSpinnersContainer)
        submitButton = findViewById(R.id.submitButton)
        saveButton = findViewById(R.id.saveButton)
        loadButton = findViewById(R.id.loadButton)
        deleteButton = findViewById(R.id.deleteButton)
        configurationsSpinner = findViewById(R.id.configurationsSpinner)
        configurationNameEditText = findViewById(R.id.configurationNameEditText)

        // Set up the spinner for selecting the number of stripes
        val stripeOptions = arrayOf("3 Stripes", "4 Stripes", "5 Stripes", "6 Stripes")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, stripeOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        stripeNumberSpinner.adapter = adapter

        stripeNumberSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedStripeOption = stripeOptions[position]
                val numStripes = selectedStripeOption.split(" ")[0].toInt()

                stripeSpinnersContainer.removeAllViews()

                // Create spinner views
                for (i in 1..numStripes) {
                    val stripeSpinner = Spinner(this@ChooseStripesActivity)
                    val stripeAdapter = ArrayAdapter(this@ChooseStripesActivity, android.R.layout.simple_spinner_item, getStripeColorOptions(i, numStripes))
                    stripeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    stripeSpinner.adapter = stripeAdapter

                    stripeSpinnersContainer.addView(stripeSpinner)

                    stripeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            // Handle the selection of color for each stripe
                            checkSubmitButtonState()

                            val selectedColor = stripeAdapter.getItem(position).toString()
                            val backgroundColor = getColorForStripe(selectedColor)

                            stripeSpinner.setBackgroundColor(backgroundColor)
                            Toast.makeText(applicationContext, "Selected Color: $selectedColor", Toast.LENGTH_SHORT).show()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }
                    }
                }
                checkSubmitButtonState()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        submitButton.setOnClickListener {
            val selectedStripes = mutableListOf<String>()
            for (i in 0 until stripeSpinnersContainer.childCount) {
                val stripeSpinner = stripeSpinnersContainer.getChildAt(i) as Spinner
                val selectedColor = stripeSpinner.selectedItem.toString()
                selectedStripes.add(selectedColor)
            }
            val intent = Intent(this, CalculateResistanceActivity::class.java)
            intent.putStringArrayListExtra("stripes", ArrayList(selectedStripes))
            startActivity(intent)
        }

        saveButton.setOnClickListener {
            val selectedStripes = mutableListOf<Int>()
            for (i in 0 until stripeSpinnersContainer.childCount) {
                val stripeSpinner = stripeSpinnersContainer.getChildAt(i) as Spinner
                val selectedColorIndex = stripeSpinner.selectedItemPosition
                selectedStripes.add(selectedColorIndex)
            }
            val configurationName = configurationNameEditText.text.toString()
            savedConfigurations.add(Pair(configurationName, selectedStripes.toList()))
            updateConfigurationsSpinner()
            saveConfigurationsToFile()
        }

        deleteButton.setOnClickListener {
            val selectedConfigurationIndex = configurationsSpinner.selectedItemPosition
            if (selectedConfigurationIndex != AdapterView.INVALID_POSITION) {
                savedConfigurations.removeAt(selectedConfigurationIndex)
                updateConfigurationsSpinner()
                saveConfigurationsToFile()
                Toast.makeText(this, "Configuration deleted", Toast.LENGTH_SHORT).show()
            }
        }

        loadButton.setOnClickListener {
            val selectedConfigurationIndex = configurationsSpinner.selectedItemPosition
            if (selectedConfigurationIndex != AdapterView.INVALID_POSITION) {
                val selectedConfiguration = savedConfigurations[selectedConfigurationIndex]
                val numStripes = selectedConfiguration.second.size
                val stripeOption = "$numStripes Stripes"
                val stripeOptionIndex = stripeOptions.indexOf(stripeOption)
                stripeNumberSpinner.setSelection(stripeOptionIndex)
                val colors = selectedConfiguration.second
                setStripeColors(colors)
            }
        }

        loadConfigurationsFromFile()
    }
    // Set the colors of the stripes
    fun getColorForStripe(color: String): Int {
        return when (color) {
            "None" -> Color.TRANSPARENT
            "Black" -> ContextCompat.getColor(this, R.color.black)
            "Brown" -> ContextCompat.getColor(this, R.color.brown)
            "Red" -> ContextCompat.getColor(this, R.color.red)
            "Orange" -> ContextCompat.getColor(this, R.color.orange)
            "Yellow" -> ContextCompat.getColor(this, R.color.yellow)
            "Green" -> ContextCompat.getColor(this, R.color.green)
            "Blue" -> ContextCompat.getColor(this, R.color.blue)
            "Purple" -> ContextCompat.getColor(this, R.color.purple)
            "Gray" -> ContextCompat.getColor(this, R.color.gray)
            "White" -> ContextCompat.getColor(this, R.color.white)
            "Silver" -> ContextCompat.getColor(this, R.color.silver)
            "Gold" -> ContextCompat.getColor(this, R.color.gold)
            else -> Color.TRANSPARENT
        }
    }

    // Check the state of the submit button based on selected colors
    fun checkSubmitButtonState() {
        var isSubmitButtonEnabled = true
        for (i in 0 until stripeSpinnersContainer.childCount) {
            val stripeSpinner = stripeSpinnersContainer.getChildAt(i) as Spinner
            val selectedColor = stripeSpinner.selectedItem.toString()
            if (selectedColor == "None") {
                isSubmitButtonEnabled = false
                break
            }
        }
        submitButton.isEnabled = isSubmitButtonEnabled
        submitButton.alpha = if (isSubmitButtonEnabled) 1.0f else 0.5f
    }

    // Update the configurations spinner with saved configuration names
    private fun updateConfigurationsSpinner() {
        val configurationNames = savedConfigurations.map { it.first }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, configurationNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        configurationsSpinner.adapter = adapter
    }

    // Set the colors of the stripes
    fun setStripeColors(colors: List<Int>) {
        if (stripeSpinnersContainer.childCount != colors.size) {
            return
        }
        for (i in 0 until stripeSpinnersContainer.childCount) {
            val stripeSpinner = stripeSpinnersContainer.getChildAt(i) as Spinner
            val colorIndex = colors[i]
            stripeSpinner.setSelection(colorIndex)
        }
    }

    // Save the configurations to a file
    fun saveConfigurationsToFile() {
        val file = File(applicationContext.filesDir, "configurations.txt")
        val fileOutputStream = FileOutputStream(file)
        val objectOutputStream = ObjectOutputStream(fileOutputStream)
        objectOutputStream.writeObject(savedConfigurations)
        objectOutputStream.close()
        fileOutputStream.close()
    }

    // Load the configurations from a file
    fun loadConfigurationsFromFile() {
        val file = File(applicationContext.filesDir, "configurations.txt")
        if (file.exists()) {
            val fileInputStream = FileInputStream(file)
            val objectInputStream = ObjectInputStream(fileInputStream)
            val configurations = objectInputStream.readObject() as MutableList<Pair<String, List<Int>>>
            savedConfigurations.clear()
            savedConfigurations.addAll(configurations)
            objectInputStream.close()
            fileInputStream.close()
        }
        updateConfigurationsSpinner()
    }

    fun getStripeColorOptions(position: Int, numStripes: Int): Array<String> {
        return when (numStripes) {
            3 -> {
                when (position) {
                    1, 2 -> normalColorOptions
                    3 -> multiplierColorOptions
                    else -> emptyArray()
                }
            }
            4 -> {
                when (position) {
                    1, 2 -> normalColorOptions
                    3 -> multiplierColorOptions
                    4 -> toleranceColorOptions
                    else -> emptyArray()
                }
            }
            5 -> {
                when (position) {
                    1, 2, 3 -> normalColorOptions
                    4 -> multiplierColorOptions
                    5 -> toleranceColorOptions
                    else -> emptyArray()
                }
            }
            6 -> {
                when (position) {
                    1, 2, 3 -> normalColorOptions
                    4 -> multiplierColorOptions
                    5 -> toleranceColorOptions
                    6 -> temperatureColorOptions
                    else -> emptyArray()
                }
            }
            else -> emptyArray()
        }
    }
}
