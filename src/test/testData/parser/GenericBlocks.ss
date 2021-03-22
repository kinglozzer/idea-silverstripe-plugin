<% cache 'Something' %>
    <% if $Foo %>
        {$Foo}
        <% cache_include 'IncludeName' %>
    <% end_if %>
<% end_cache %>