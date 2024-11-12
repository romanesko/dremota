{#if commands && commandTypes}
  {#each commands as command,idx}
    <div class="col-4 mb-2">

      <CommandComponent command={command} types={commandTypes}  onResize={idx===commands.length-1?onResize:undefined}/>
    </div>
  {/each}

  <div class="col-4 mb-2">
      <div class="card-body cmd-hint" >
        <div class="form-label mb-2 text-center pe-3" style="font-size: .875rem;">Возможная разметка сообщений:</div>
        <div style="margin: 0 auto" class="my-3">
        <HtmlHint/>
          </div>
        <button type="submit" class="btn btn-primary" style="margin:0 auto" onclick={addCommand}>Добавить команду</button>

      </div>

  </div>

{/if}


<script lang="ts">

    import CommandComponent from './Command.svelte'

    import type {Command} from "@/models.js";


    import api from "@/lib/api";
    import {alertError} from "@/lib/common";
    import HtmlHint from "@/pages/settings/components/HtmlHint.svelte";

    let commands: Command[] | undefined = $state(undefined)
  let commandTypes: {[key:string]:string} | undefined = $state(undefined)

  api.getCommandTypes().then(a => commandTypes = a)

  api.getCommands().then(a => {
    commands = a
  })

  function addCommand() {
    if (!commands) {
      return
    }
    let key = prompt("Имя команды?")
    if(!key){
      return
    }

    if (key.indexOf('/')==0){
      key = key.slice(1)
    }

    if (!/^[a-z0-9_]+$/.test(key)){
      alertError('Команда может состоять только из\n- латинских букв\n- цифр\n- символов "_"')
      return
    }

    const cmd = {key,
      description: "",
      type: "MESSAGE",
      message: "",
      showInMenu: false,
      enabled: true,
      system: false,
      isNew: true
    } as Command

    commands = [...commands, cmd]

  }

  function onResize(height: number) {

    const el2 = document.querySelector('.cmd-hint') as HTMLElement
    if (!el2) return
    el2.style.height = height + 'px'
  }


</script>

<style>
  .cmd-add-wrapper {
    display: flex; flex-direction: column; justify-content: center;
  }
  .cmd-hint {
    display: flex; flex-direction: column; justify-content: center;
  }
</style>