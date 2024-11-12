{#key params.chatId}
  {#await user then user}

    <div class="col-4">
      <UserInfo user={user}/>
    </div>

    <div class="col-8">
      <Messenger user={user}/>
    </div>

    <div class="col-4">
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">Оплаты</h3>
        </div>
        <PaymentsTable userId={params.chatId} selectedPaymentId={selectedPaymentId}/>
      </div>
    </div>

    <div class="col-8">
      <div class="card">
        <div class="card-header flex-row justify-content-between">
          <h3 class="card-title">Пополнения</h3>
          <button type="button" class="btn btn-sm" onclick={showAddBalanceModal}>
            Добавить пополнение вручную
          </button>
        </div>
        {#key balanceKey}
          <BalanceTable userId={params.chatId} onBalanceClick={onBalanceClick}/>
        {/key}
      </div>
    </div>


    <div class="col-12">
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">Запросы пользователя</h3>
        </div>
        <QueriesTable userId={params.chatId}/>
      </div>
    </div>


    <div class="modal modal-blur fade" bind:this={addBalanceModal} tabindex="-1">
      <AddBalanceForm userId={params.chatId} onSubmit={onAddBalance}/>
    </div>
  {/await}

{/key}


<script lang="ts">


  import QueriesTable from "./components/QueriesTable.svelte";
  import PaymentsTable from "./components/PaymentsTable.svelte";
  import BalanceTable from "./components/BalanceTable.svelte";
  import AddBalanceForm from "./components/AddBalanceForm.svelte";
  import Messenger from "@/pages/user/components/Messenger.svelte";
  import UserInfo from "@/pages/user/components/UserInfo.svelte";
  import api from "@/lib/api";
  import * as bootstrap from "bootstrap";
  import {onDestroy, onMount} from "svelte";

  let {params}: { params: { chatId: number } } = $props()

  let selectedPaymentId: number | undefined = $state()

  let addBalanceModal: HTMLElement | undefined = $state(undefined)

  function onBalanceClick(paymentId: number) {
    selectedPaymentId = paymentId
  }

  let user = api.getUser(params.chatId)


  let balanceKey = $state(0)

  let bootstrapModal :any;

  onMount(() => {
    setTimeout(() => {
      bootstrapModal = bootstrap.Modal.getOrCreateInstance(addBalanceModal)
    }, 1000)


  });

  onDestroy(() => {
    bootstrapModal.dispose();
  });



  function showAddBalanceModal() {
    bootstrapModal.show()
  }

  function onAddBalance() {
    selectedPaymentId = undefined
    bootstrapModal.hide()
    balanceKey++
  }

</script>

