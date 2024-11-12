<!--<table class="table table-sm table-bordered"><tbody>-->
<!--{#each htmlHint as hint}-->
<!--  <tr>-->
<!--    <td>&lt;{hint.value}&gt;{hint.name}&lt;/{hint.value}&gt;</td>-->
<!--    <td>{@html render(hint)}</td>-->
<!--  </tr>-->
<!--{/each}-->
<!--{#each variables as v}-->
<!--  <tr>-->
<!--    <td>&lbrace;&lbrace;{v.name}&rbrace;&rbrace;</td>-->
<!--    <td>{@html v.value}</td>-->
<!--  </tr>-->
<!--{/each}-->
<!--</tbody>-->
<!--</table>-->

<div>
  {#each htmlHint as hint}
    <div class="row">
      <div class="col" style="min-width:200px">
        &lt;{hint.value}&gt;{hint.name}&lt;/{hint.value}&gt;
      </div>
      <div class="col-auto">
        {@html render(hint)}
      </div>
    </div>
  {/each}
  {#each variables as v}
    <div class="row">
      <div class="col">
        &lbrace;&lbrace;{v.name}&rbrace;&rbrace;
      </div>
      <div class="col-auto">
        {@html v.value}
      </div>
    </div>
  {/each}

</div>

<script lang="ts">
  type Hint = {name: string, value: string}

  const htmlHint = [
    {name: 'bold', value: 'b'},
    {name: 'italic', value: 'i'},
    {name: 'strike', value: 's'},
    {name: 'underline', value: 'u'},
    {name: 'code', value: 'pre'},
  ] as Hint[]

  function render(h: Hint) {
    return `<${h.value} class="unmark">${h.name}</${h.name}>`
  }

  const variables = [
      {name:'paid_until', value:'23.12.2024 16:07'},
      {name:'username', value:'@ivanov_ivan'},
      {name:'first_name', value:'Иван'},
      {name:'last_name', value:'Иванов'},
      {name:'referral', value:'<a href="https://t.me/DremotaBot?start=generatedRef">DremotaBot</a>'},
  ]

</script>
