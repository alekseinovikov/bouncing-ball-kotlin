import java.awt.Color
import java.awt.Graphics
import java.awt.Point
import java.awt.Toolkit
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.Timer
import kotlin.random.Random.Default.nextInt
import kotlin.system.exitProcess


fun main(args: Array<String>) {
    JFrame("Universe").apply {
        extendedState = JFrame.MAXIMIZED_BOTH
        add(MainApp(this, 7, 20, 5, 1000))
        isResizable = true
        isUndecorated = true
        isVisible = true

        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent?) {
                if (e?.keyCode == KeyEvent.VK_ESCAPE) {
                    dispose()
                    exitProcess(0)
                }
            }
        })

        val cursorImg = BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)
        this.cursor = Toolkit.getDefaultToolkit().createCustomCursor(
            cursorImg, Point(0, 0), "blank cursor"
        )
    }
}

class MainApp(
    private val frame: JFrame,
    timerDelay: Int,
    private val maxSpeed: Int,
    private val minSpeed: Int,
    private val ballsCount: Int
) : JPanel(), ActionListener {

    private val ballWidth = 10
    private val ballHeight = 10
    private var balls: List<Ball>? = null

    init {
        background = Color.BLACK
        Timer(timerDelay, this).start()
    }


    override fun paint(graphics: Graphics) {
        super.paint(graphics)

        if (balls == null) balls = generateBalls(ballsCount) //It's better to generate after full system init
        balls?.forEach {
            it.move()
            graphics.color = it.currentColor
            graphics.fillOval(it.xp, it.yp, it.width, it.height)
        }
    }


    override fun actionPerformed(e: ActionEvent?) = repaint()

    private fun generateBalls(count: Int): List<Ball> {
        val box = object : Box {
            override val width: Int
                get() = frame.width
            override val height: Int
                get() = frame.height
        }

        return generateSequence {
            val xp = nextInt(box.width) + 1
            val yp = nextInt(box.height) + 1
            val randXSpeed = nextInt(maxSpeed) + 1
            val randYSpeed = nextInt(maxSpeed) + 1
            val xSpeed = if (randXSpeed < minSpeed) minSpeed else randXSpeed
            val ySpeed = if (randYSpeed < minSpeed) minSpeed else randYSpeed

            val height = nextInt(ballHeight) + 1
            val width = nextInt(ballWidth) + 1

            Ball(xp, yp, xSpeed, ySpeed, height, width, box)
        }.take(count).toList()
    }

}

interface Box {
    val width: Int
    val height: Int
}

class Ball(
    var xp: Int, var yp: Int,
    private var xSpeed: Int,
    private var ySpeed: Int,
    val height: Int,
    val width: Int,
    private val box: Box
) {

    var currentColor: Color = randomizeColor()
    private var xDirection = 1
    private var yDirection = 1

    fun move() {
        if (xp + width > box.width) {
            xDirection = -1
            changeColor()
        } else if (xp < 0) {
            xDirection = 1
            changeColor()
        }

        if (yp + height > box.height) {
            yDirection = -1
            changeColor()
        } else if (yp < 0) {
            yDirection = 1
            changeColor()
        }

        xp += (xSpeed * xDirection)
        yp += (ySpeed * yDirection)
    }

    private fun changeColor() {
        this.currentColor = randomizeColor()
    }

    private fun randomizeColor(): Color {
        val r = nextInt(256)
        val g = nextInt(256)
        val b = nextInt(256)
        return Color(r, g, b)
    }
}