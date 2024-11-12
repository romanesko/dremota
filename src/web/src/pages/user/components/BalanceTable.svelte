{#await pBalance then items}

<div class="table-responsive">
  <table class="table table-vcenter card-table">
    <thead>
    <tr>
      <th class="text-center">добавлео</th>
      <th class="text-center">дней</th>
      <th class="text-center">до</th>
      <th class="">обоснование</th>
      <th class="text-center">платёж</th>
    </tr>
    </thead>
    <tbody>
    {#each items as item}
      <tr>
        <td class="text-secondary text-center">{item.createdAt}</td>
        <td class="text-secondary text-center">{item.days}</td>
        <td class="text-secondary text-center">{item.due}</td>
        <td class="text-secondary ">{item.rationale}</td>
        <td class="text-secondary text-center">
          {#if item.paymentId}
            <a href="#/" onclick={(e)=>paymentClick(e, item.paymentId)}>№{item.paymentId}</a>
            {:else}
            <div>нет</div>
            {/if}
        </td>
      </tr>
    {/each}

    </tbody>
  </table>
</div>
  {:catch error}
  <div class="alert alert-danger" role="alert">
    {error}
  </div>
{/await}

<script lang="ts">
    import api from "@/lib/api";
    import {log} from "@/lib/common";

    const {userId, onBalanceClick} : {userId: number, onBalanceClick: (paymentId: number) => void} = $props()

  let pBalance = api.getBalanceForUser(userId)

  function paymentClick(e: Event, paymentId: number) {
    e.preventDefault();
    log('paymentId', paymentId)
    onBalanceClick(paymentId)
  }

</script>