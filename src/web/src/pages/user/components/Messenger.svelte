

{#if users}
  <div class="card">
    <div id="hint" style="display:none">
      <HtmlHint/>
    </div>

    <div class="card-body scrollable" bind:this={chatBody} style="height: 20rem">
      <div class="chat" onclick={handleChatClick} aria-hidden="true">
        <div class="chat-bubbles">
          {#each chatItems as item}
            {#if item.in}

              <div class="chat-item">
                <div class="row align-items-end">
                  <div class="col-auto">
                    <span class="avatar" style="background-image: url({getUserPhoto(item.user)})">
                      {#if !item.read}<span class="badge bg-red"></span>{/if}
                    </span>
                  </div>
                  <div class="col col-lg-6">
                    <div class="chat-bubble">
                      <div class="chat-bubble-title">
                        <div class="row">
                          <div class="col chat-bubble-author">{item.user.displayName}</div>
                          <div class="col-auto chat-bubble-date">{item.date}</div>
                        </div>
                      </div>
                      <div class="chat-bubble-body">
                        <p>{@html item.message}</p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>


            {:else}

              <div class="chat-item">
                <div class="row align-items-end justify-content-end">
                  <div class="col col-lg-6">
                    <div class="chat-bubble chat-bubble-me">
                      <div class="chat-bubble-title">
                        <div class="row">
                          <div class="col chat-bubble-author">{item.user.displayName}</div>
                          <div class="col-auto chat-bubble-date">{item.date}</div>
                        </div>
                      </div>
                      <div class="chat-bubble-body">
                        {@html item.message}
                      </div>
                    </div>
                  </div>
                  <div class="col-auto"><span class="avatar" style="background-image: url({getUserPhoto(item.user)})"></span>
                  </div>
                </div>
              </div>
            {/if}
          {/each}

        </div>
      </div>
    </div>
    <div class="card-footer">
      <div class="input-group input-group-flat">


        <textarea
            bind:this={popoverTrigger}
            bind:value={message}
            class="form-control"

            placeholder="Спасибо за ваш отзыв. Если хотите что-то добавить, отправьте команду /feedback"
            style="overflow: hidden; overflow-wrap: break-word; resize: none; text-align: start; height: 60px;"></textarea>
        <span class="input-group-text">
          {#if sending}
                  <div class="spinner-border"></div>
            {:else}
                        <button
                            onclick={sendMessage}
                            class="btn btn-ghost-primary ms-2 send-button"
                            data-bs-toggle="tooltip"
                            aria-label="Add notification"
                            data-bs-original-title="Add notification">
                          <img src={SendIcon} alt="send" style="width: 1rem; height: 1rem; color:white"/>

                        </button>
            {/if}
                      </span>


      </div>
    </div>
  </div>

{/if}
<script lang="ts">
  import api from "@/lib/api";
  import type {Message, User} from "@/models";
  import {alertError, formatDate, getUserPhoto} from "@/lib/common";

  import {currentUser} from "@/store/user";
  import * as bootstrap from "bootstrap";
  import HtmlHint from "@/pages/settings/components/HtmlHint.svelte";
  import SendIcon from '@/assets/send.svg';
  import {onMount} from "svelte";

  let message = $state('')
  let chatBody: HTMLElement | undefined = $state(undefined)

  let popoverTrigger: HTMLElement | undefined = $state(undefined)

  onMount(() => {

    const data = document.getElementById('hint')?.innerHTML
    new bootstrap.Popover(popoverTrigger, {
      content: data,
      html: true,
      trigger: 'focus',
      placement: 'left'
    });


    // popover.show();
  })


  const {user}: { user: User } = $props()

  let isFirstLoad = $state(true)

  let users: Map<number, User> | undefined = $state(new Map<number, User>())

  interface ChatItem {
    id?: number,
    user: User,
    message: string,
    in: boolean,
    date: string,
    read: boolean
  }

  let chatItems: ChatItem[] = $state([])

  let sending = $state(false)

  function scrollToBottom() {
    if (!chatBody) return

    if (isFirstLoad) {
      chatBody.scroll({top: chatBody.scrollHeight});
      isFirstLoad = false;
    } else {
      chatBody.scroll({top: chatBody.scrollHeight, behavior: 'smooth'});
    }
  }

  $effect(() => {
    if (!chatBody) return
    chatItems.length && setTimeout(scrollToBottom, 100)
  })


  function handleChatClick(e: MouseEvent) {
    if (!chatItems) {
      return
    }
    chatItems.filter(i => i.id && !i.read).forEach(async item => {
      await markRead(item.id!)
      item.read = true
    })

  }

  async function getChat() {
    const localChatItems = []

    const feedbackItems = await api.getFeedbackForUser(user.chatId);
    const messages = await api.getMessagesForUser(user.chatId);

    const senders = new Set(messages.map(m => m.senderId))

    const sendersHash = new Map<number, User>()

    for (const s of senders) {
      const user = await api.getUser(s);
      sendersHash.set(s, user)
    }

    for (const m of messages) {
      const user = sendersHash.get(m.senderId)
      if (!user) continue
      localChatItems.push({
        user: user,
        message: m.message,
        in: false,
        date: formatDate(m.createdAt),
        read: true
      })
    }


    for (const f of feedbackItems) {
      localChatItems.push({
        id: f.id,
        user: user,
        message: f.message,
        in: true,
        date: formatDate(f.createdAt),
        read: f.read
      })
    }

    chatItems = localChatItems.sort((a, b) => {
      return a.date.localeCompare(b.date)
    })


  }


  getChat()


  function markRead(id: number) {
    if (!id) return
    return api.markFeedbackRead(id).catch(alertError)
  }

  async function sendMessage() {
    if (!$currentUser) {
      return
    }

    sending = true
    try {

      const msg = {
        senderId: $currentUser.chatId,
        receiverId: user.chatId,
        message: message.trim(),
        createdAt: formatDate(new Date().toISOString()),
      } as Message


      await api.addMessage(msg)

      getChat()
      // if (!chatItems) return
      // chatItems.push({
      //   user: $currentUser,
      //   message: message.trim(),
      //   in: false,
      //   date: msg.createdAt,
      //   read: false
      // })

      message = ''
    } catch (e) {
      alertError(e)
    } finally {
      sending = false
    }


  }

</script>
<style>
  .send-button:hover {
    background-color: transparent;

  }
</style>