{#if command}
  <div class="card command-card" use:watchResize={handleResize}>
    <div class="card-header flex-row align-items-center justify-content-between gap-2">

          <div>
            <input class="form-check-input" type="checkbox" bind:checked={command.enabled}>
          </div>
          <div style="font-size:16px;font-weight:500; padding-bottom:1px">/{command.key}</div>
          <div style="font-size: 0.6rem;opacity: 0.7;">
            [{types[command.type].toUpperCase()}]
          </div>

            <div class="flex-grow-1 text-end text-nowrap" >
              {#if !command.system && !command.isNew}
                <button class="btn btn-sm btn-danger" onclick={deleteCommand}>
                  <img src={trash} alt="trash" style="width: 1rem; height: 1rem; color:white"/>
                </button>
              {/if}
              <button class="btn btn-sm btn-outline-primary" onclick={saveCommand}>
                Сохранить
              </button>
            </div>



    </div>
    <div class="card-body" class:hidden={!command.enabled}>


      <div class="mb-2">
        <label class="form-label">
          <input class="form-check-input" type="checkbox" bind:checked={command.showInMenu}>
          Отображать команду в меню со следующим описанием:</label>

        <input
            class:hidden={!command.showInMenu} bind:value={command.description} class="form-control" placeholder=""/>

      </div>


      <div>
        <label class="form-label">Сообщение</label>
        <small class="form-hint text-nowrap" style="overflow: hidden; text-overflow: ellipsis">Будет отправлено пользователю при вызове команды</small>

        <textarea
            bind:value={command.message}
            class="form-control msg-textarea"
            class:post-message={hasPostMessage(command)}
            data-bs-toggle="autosize"
            placeholder=""
            ></textarea>

      </div>

      {#if hasPostMessage(command)}
      <div class="mt-2">
        <label class="form-label">Сообщение по завершению</label>
        <small class="form-hint text-nowrap" style="overflow: hidden; text-overflow: ellipsis">Будет отправлено в случае успеха</small>

        <textarea
            bind:value={command.postMessage}
            class="form-control msg-textarea post-message"
            data-bs-toggle="autosize"
            placeholder=""
        ></textarea>

      </div>





      {/if}


    </div>

  </div>
{/if}

<script lang="ts">
    import type {Command} from "@/models";
    import {watchResize} from "svelte-watch-resize";

    import trash from '@/assets/trash.svg';


    import api from "@/lib/api";
    import {alertError, alertSuccess} from "@/lib/common";

    let {command, types,  onResize}: {
    command: Command | undefined,
    types: { [key: string]: string },
    onResize?: (height: number) => void
  } = $props()

  function hasPostMessage(command: Command) {
    return command.system && command.type !== 'START'
  }


  function handleResize(node:  HTMLElement) {
    if (!onResize) return
    onResize(node.clientHeight)
  }

  function showSavedToast(a: any) {
    if (a) alertSuccess('Команда успешно сохранена')


  }

  function saveCommand() {
    if (!command) {
      return
    }
    if (command.isNew) {

      api.addCommand(command).catch(alertError).then(showSavedToast)
    } else {
      api.updateCommand(command).catch(alertError).then(showSavedToast)
    }

  }

  function deleteCommand() {
    if (!command) {
      return
    }
    if (!confirm('Вы уверены, что хотите удалить команду?')) return
    api.deleteCommand(command).catch(alertError).then(a => {

      if (a) {
        command = undefined
        alertSuccess('Команда успешно удалена')
      }
    })
  }

</script>
<style>
  .hidden {
    opacity: 0.2;
  }
  .msg-textarea {
    overflow: hidden;
    overflow-wrap: break-word;
    /*resize: none;*/
    text-align: start;
    height: 173px;
  }
  .msg-textarea.post-message {
    height: 60px;
  }

</style>