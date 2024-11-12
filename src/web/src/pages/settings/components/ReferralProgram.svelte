{#if referral}

  <div class="mb-3">

    <label class="form-check form-switch">
      <input class="form-check-input" type="checkbox" bind:checked={referral.enabled}>
      <span class="form-check-label">Реферальная программа включена</span>
    </label>

  </div>

  <div class="row mb-3">
    <div class="col">
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">Начисление отправителю</h3>
        </div>
        <div class="card-body">
          <div class="row mb-3">
            <div class="col-auto">
              <label class="form-label">Дни</label>
              <input
                  type="number"
                  class="days-count form-control"
                  bind:value={referral.holderReceives}
                  placeholder={String(defaults.holderReceives)}>
            </div>
            <div class="col">
              <label class="form-label">Сообщение после начисления</label>
              <input
                  type="text"
                  class="form-control"
                  bind:value={referral.holderMessage}
                  placeholder={defaults.holderMessage}>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="col">
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">Начисление получателю</h3>
        </div>
        <div class="card-body">
          <div class="row mb-3">
            <div class="col-auto">
              <label class="form-label">Дни</label>
              <input
                  type="number"
                  class="days-count form-control"
                  bind:value={referral.referrerReceives}
                  placeholder={String(defaults.referrerReceives)}>
            </div>
            <div class="col">
              <label class="form-label">Сообщение после начисления</label>
              <input
                  type="text"
                  class="form-control"
                  bind:value={referral.referrerMessage}
                  placeholder={defaults.referrerMessage}>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>


  <div class="row">
    <div class="col"></div>
    <div class="col-auto">
      <button class="btn btn-primary" onclick={save}>
        Сохранить настройки реферальной программы
      </button>
    </div>
  </div>


{/if}


<script lang="ts">

    import type {Referral} from "@/models.js";

    import api from "@/lib/api";
    import {alertError, alertSuccess} from "@/lib/common";

    const defaults = {
    holderReceives: 5,
    holderMessage: 'Кто-то зарегистрировался по вашей ссылке, вам 5 дней в подарок',
    referrerMessage: 'Вам в подарок по реферальной программе 5 дней',
    referrerReceives: 5,
  }


  let referral: Referral | undefined = $state()

  api.getReferral().catch(alertError).then(a => {
    if (a) referral = a
  })


  function save() {
    if (!referral) {
      return
    }
    api.saveReferral(referral).catch(alertError).then(a => {
      if (a) {
        alertSuccess('Настройки реферальной программы успешно сохранены')
      }
    })
  }


</script>
<style>
  .days-count {
    width: 65px;
    text-align: center;
  }
</style>