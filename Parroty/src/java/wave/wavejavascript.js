<?xml version="1.0" encoding="UTF-8" ?>
<Module>
<ModulePrefs title="State Example" height="120">
    <Require feature="wave" />
  </ModulePrefs>
<Content type="html">
<![CDATA[
<div id="content_div" style="height: 50px;"></div>

    <script type="text/javascript">

    var div = document.getElementById('content_div');

    function buttonClicked() {
      var value = parseInt(wave.getState().get('count', '0'));
      wave.getState().submitDelta({'count': value + 1});
    }

    // Reset value of "count" to 0
    function resetCounter(){
      wave.getState().submitDelta({'count': '0'});
      wave.log("I just reset the count to zero.");
    }

    function stateUpdated() {
      if(!wave.getState().get('count')) {
        div.innerHTML = "The count is 0."
      }
      else {
        div.innerHTML = "The count is " + wave.getState().get('count');
        wave.log("The count is " + wave.getState().get('count'));
      }
    }

    function init() {
      if (wave && wave.isInWaveContainer()) {
        wave.setStateCallback(stateUpdated);
      }
    }
    gadgets.util.registerOnLoadHandler(init);

    </script>
    <input type=button value="Click Me!" id="butCount" onClick="buttonClicked()">
    <input type=button value="Reset" id="butReset" onClick="resetCounter()">
  ]]>
  </Content>
</Module>