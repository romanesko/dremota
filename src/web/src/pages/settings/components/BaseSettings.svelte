{#if settings}


  <div class="mb-3">
    <label class="form-label required">Контекст</label>
    <small class="form-hint">Контекст запроса в ИИ</small>
    <input
        type="text"
        class="form-control"
        bind:value={settings.context}
        placeholder="Ты профессиональный толкователь снов">

  </div>
  <div class="mb-3">
    <label class="form-label">Префикс к запросу</label>
    <small class="form-hint">То, что будет подставляться перед каждым запросом</small>
    <input
        type="text"
        class="form-control"
        bind:value={settings.prefix}
        placeholder="Растолкуй мне сновидение подробно. Мне приснился такой сон:">
  </div>
  <div class="mb-3">
    <label class="form-label">Постфикс к ответу</label>
    <small class="form-hint">То, что будет подставляться после каждого ответа ИИ</small>
    <textarea
        bind:value={settings.post}
        class="form-control"
        data-bs-toggle="autosize"
        placeholder="Вы можете оставить отзыв через комманду /feedback"
        style="overflow: hidden; overflow-wrap: break-word; resize: none; text-align: start; height: 60px;"></textarea>
  </div>
  <div class="mb-3">
    <label class="form-label">Сообщение при пустом балансе</label>
    <textarea
        bind:value={settings.emptyBalanceMessage}
        class="form-control"
        data-bs-toggle="autosize"
        placeholder="Вы можете оставить отзыв через комманду /feedback"
        style="overflow: hidden; overflow-wrap: break-word; resize: none; text-align: start; height: 60px;"></textarea>
  </div>

  <div class="col"></div>
  <div class="col-auto">
    <button class="btn btn-primary" onclick={saveSettings}>
      Сохранить настройки
    </button>
  </div>



{/if}


<script lang="ts">

    import type {Settings} from "@/models.js";

    import api from "@/lib/api";
    import {alertError, alertSuccess} from "@/lib/common";

    let settings: Settings | undefined = $state(undefined)

  api.getSettings().then(a => {
    settings = a
  })

  function saveSettings() {
    if (!settings) {
      return
    }
    api.saveSettings(settings).catch(alertError).then(a => {
      if (a) {
        alertSuccess('Настройки успешно сохранены')

      }
    })
  }


</script>