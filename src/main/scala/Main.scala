package com.wix.nadavwe.goos.e2e

import javax.swing.{JFrame, SwingUtilities}

class Main {
  private var ui : MainWindow = _
  startUserInterface()

  private def startUserInterface() {
    SwingUtilities.invokeAndWait(new Runnable {
      override def run(): Unit =  {ui = new MainWindow}
    })
  }
}

class MainWindow extends JFrame("Auction Sniper") {
  import Main._
  setName(MAIN_WINDOW_NAME)
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  setVisible(true)
}


object Main extends App {
  main()

  def main(args: String*): Unit = { new Main() }

  val MAIN_WINDOW_NAME: String = "Sniper Window"
  val SNIPER_STATUS_NAME: String = "b"

}
