{#await pPayments then payments}
<div class="table-responsive">
  <table class="table table-vcenter card-table">
    <thead>
    <tr>
      <th class="text-center">получено</th>
      <th class="text-center">сумма</th>
      <th class="text-center">валюта</th>
    </tr>
    </thead>
    <tbody>
    {#each payments as payment}
      <tr class:selected={payment.id === selectedPaymentId}>
        <td class="text-secondary text-center">{formatDate(payment.createdAt)}</td>
        <td class="text-secondary text-center">{payment.amount}</td>
        <td class="text-secondary text-center">{payment.currency}</td>
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
    import {formatDate} from "@/lib/common";

    const {userId, selectedPaymentId} : {userId: number, selectedPaymentId: number | undefined} = $props()

  let pPayments = api.getPaymentsForUser(userId)

</script>

<style>
  tr.selected {
    background-color: rgba(255, 0, 0, 0.1);
  }
</style>