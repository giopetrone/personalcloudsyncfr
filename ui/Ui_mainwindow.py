# -*- coding: utf-8 -*-

# Form implementation generated from reading ui file '/home/marino/Python/ui/mainwindow.ui'
#
# Created: Fri Feb 24 10:25:23 2012
#      by: PyQt4 UI code generator 4.8.3
#
# WARNING! All changes made in this file will be lost!

from PyQt4 import QtCore, QtGui

try:
    _fromUtf8 = QtCore.QString.fromUtf8
except AttributeError:
    _fromUtf8 = lambda s: s

class Ui_MainWindow(object):
    def setupUi(self, MainWindow):
        MainWindow.setObjectName(_fromUtf8("MainWindow"))
        MainWindow.resize(675, 600)
        self.centralWidget = QtGui.QWidget(MainWindow)
        self.centralWidget.setObjectName(_fromUtf8("centralWidget"))
        self.gridLayout = QtGui.QGridLayout(self.centralWidget)
        self.gridLayout.setObjectName(_fromUtf8("gridLayout"))
        self.txtUrl = QtGui.QLineEdit(self.centralWidget)
        self.txtUrl.setObjectName(_fromUtf8("txtUrl"))
        self.gridLayout.addWidget(self.txtUrl, 0, 1, 1, 1)
        self.btnNavigate = QtGui.QPushButton(self.centralWidget)
        self.btnNavigate.setObjectName(_fromUtf8("btnNavigate"))
        self.gridLayout.addWidget(self.btnNavigate, 0, 2, 1, 1)
        self.webView = QtWebKit.QWebView(self.centralWidget)
        self.webView.setUrl(QtCore.QUrl(_fromUtf8("about:blank")))
        self.webView.setObjectName(_fromUtf8("webView"))
        self.gridLayout.addWidget(self.webView, 1, 1, 1, 1)
        MainWindow.setCentralWidget(self.centralWidget)

        self.retranslateUi(MainWindow)
        QtCore.QMetaObject.connectSlotsByName(MainWindow)

    def retranslateUi(self, MainWindow):
        MainWindow.setWindowTitle(QtGui.QApplication.translate("MainWindow", "MainWindow", None, QtGui.QApplication.UnicodeUTF8))
        self.btnNavigate.setText(QtGui.QApplication.translate("MainWindow", "Navigate", None, QtGui.QApplication.UnicodeUTF8))

from PyQt4 import QtWebKit

if __name__ == "__main__":
    import sys
    app = QtGui.QApplication(sys.argv)
    MainWindow = QtGui.QMainWindow()
    ui = Ui_MainWindow()
    ui.setupUi(MainWindow)
    MainWindow.show()
    sys.exit(app.exec_())

