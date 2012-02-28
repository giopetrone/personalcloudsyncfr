# -*- coding: utf-8 -*-

"""
Module implementing MainWindow.
"""

from PyQt4.QtGui import QMainWindow
from PyQt4.QtCore import pyqtSignature

from Ui_mainwindow import Ui_MainWindow

from PyQt4.QtCore import   QUrl

class MainWindow(QMainWindow, Ui_MainWindow):
    """
    Class documentation goes here.
    """
    def __init__(self, parent = None):
        """
        Constructor
        """
        QMainWindow.__init__(self, parent)
        self.setupUi(self)
    
    @pyqtSignature("")
    def on_btnNavigate_released(self):
        """
        Slot documentation goes here.
        """
        # TODO:completare
        theUrl = self.txtUrl.text()
        if theUrl[0:7] != 'http://':
             theUrl = 'http://' + theUrl
        self.webView.setUrl(QUrl(theUrl))
      # raise NotImplementedError
    
    @pyqtSignature("QString")
    def on_webView_titleChanged(self, title):
        """
        Slot documentation goes here.
        """
        # TODO: not implemented yet
        self.setWindowTitle(title)
    
    @pyqtSignature("QUrl")
    def on_webView_urlChanged(self, url):
        """
        Slot documentation goes here.
        """
        # TODO: not implemented yet
        self.txtUrl.setText(url.toString())
