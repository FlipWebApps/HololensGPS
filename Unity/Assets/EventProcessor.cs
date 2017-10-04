using System;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class EventProcessor : MonoBehaviour
{
    public Text Text;

    public void QueueEvent(Action action)
    {
        lock (m_queueLock)
        {
            m_queuedEvents.Add(action);
        }
    }

    public void ShowData(GPS_DataPacket data)
    {
        Text.text = data.ToString();
    }

    void Update()
    {
        MoveQueuedEventsToExecuting();
        while (m_executingEvents.Count > 0)
        {
            Action e = m_executingEvents[0];
            m_executingEvents.RemoveAt(0);
            e();
        }
    }
    private void MoveQueuedEventsToExecuting()
    {
        lock (m_queueLock)
        {
            while (m_queuedEvents.Count > 0)
            {
                Action e = m_queuedEvents[0];
                m_executingEvents.Add(e);
                m_queuedEvents.RemoveAt(0);
            }
        }
    }
    private System.Object m_queueLock = new System.Object();
    private List<Action> m_queuedEvents = new List<Action>();
    private List<Action> m_executingEvents = new List<Action>();
}