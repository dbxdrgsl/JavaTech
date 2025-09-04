<!doctype html><meta charset="utf-8">
<title>Image Repository Report</title>
<h2>Images (${items?size})</h2>
<table border="1" cellpadding="6" cellspacing="0">
  <tr><th>Name</th><th>Date</th><th>Tags</th><th>Path</th></tr>
  <#list items as it>
    <tr>
      <td>${it.name}</td>
      <td>${it.date}</td>
      <td><#list it.tags as t><span>${t}</span><#if t_has_next>, </#if></#list></td>
      <td>${it.path}</td>
    </tr>
  </#list>
</table>
