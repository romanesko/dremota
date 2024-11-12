<div class="modal-dialog modal-lg" role="document">
  <div class="modal-content">
    <div class="modal-header">
      <h5 class="modal-title">Пополнение баланса</h5>
      <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
    </div>
    <div class="modal-body">
      <div class="mb-3">
        <label class="form-label">Количество дней</label>
        <input
            type="number"
            bind:value={days}
            class="form-control"
            name="example-text-input"
            placeholder="например: 10"/>
      </div>

      <div class="mb-3">
        <label class="form-label">Причина пополнения</label>
        <input
            type="text"
            bind:value={rationale}
            class="form-control"
            name="example-text-input"
            placeholder="например: подарок, промо, оплата вне сервиса"/>
      </div>


    </div>
    <div class="modal-footer">
      <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Отмена</button>
      <button type="button" class="btn btn-primary" onclick={submit}>Добавить</button>
    </div>
  </div>
</div>

<script lang="ts">

    import api from "@/lib/api";
    import {alertError} from "@/lib/common";

    let days = $state('')
  let rationale = $state('')

  const {userId, onSubmit}: { userId: number, onSubmit: () => void } = $props()

  function submit() {
    api.addBalance(userId, days, rationale).catch(alertError).then(a => {
      if (a) {
        onSubmit()
      }
    })
  }


</script>