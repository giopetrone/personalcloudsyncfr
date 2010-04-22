<!--== Load =================================================-->
<div id="listDiv">
  <p class="para">
  <table id="diagramList">
    <thead>
      <tr>
        <td width="20%">Name</td>
        <td width="40%">Description</td>
        <td width="15%">Created</td>
        <td width="15%">Modified</td>
      </tr>
    </thead>
    <tbody onclick="selectRow(event)">
    </tbody>
  </table>
  </p>
  <p class="para">
    <button onclick="doLoad()">Load</button>
    <button onclick="doDelete()">Delete</button>
  </p>
</div>
