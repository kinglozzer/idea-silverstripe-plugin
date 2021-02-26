<% if $Foo && $FooBar %>
    {$Foo}
<% else_if $Bar %>
    <% if $Bar.Baz %>
        {$Bar.Baz}
    <% end_if %>
<% end_if %>